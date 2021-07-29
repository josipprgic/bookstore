package bookstore.repository;

import bookstore.integration.IntegrationTestBase;
import org.huddle.bookstore.jprgic.enums.BookType;
import org.huddle.bookstore.jprgic.tables.records.UserRecord;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.groups.Tuple.tuple;
import static org.huddle.bookstore.jprgic.enums.BookType.NEW_RELEASE;
import static org.huddle.bookstore.jprgic.enums.BookType.REGULAR;
import static org.huddle.bookstore.jprgic.tables.Book.BOOK;
import static org.huddle.bookstore.jprgic.tables.Purchase.PURCHASE;
import static org.huddle.bookstore.jprgic.tables.User.USER;

class UserRepositoryIT extends IntegrationTestBase {

    @Autowired
    private UserRepository userRepository;

    @Test
    void insertNewUser_should_insertNewUser() {
        userRepository.insertNewUser("jprgic");

        assertThat(fetchAllUsers()).extracting("username").containsExactly("jprgic");
    }

    @Test
    void insertNewUser_should_throw_when_insertingDuplicateUsernames() {
        userRepository.insertNewUser("jprgic");

        assertThatThrownBy(() -> userRepository.insertNewUser("jprgic")).isExactlyInstanceOf(DuplicateKeyException.class);
    }

    @Test
    void getLoyaltyPoints_should_returnLoyaltyPointsForUser() {
        UserRecord userRecord = new UserRecord()
                .setUsername("jprgic")
                .setLoyaltyPoints(23);
        dslContext.attach(userRecord);
        userRecord.insert();

        assertThat(userRepository.getLoyaltyPoints("jprgic")).isEqualTo(23);
    }

    @Test
    void getLoyaltyPoints_should_returnNull_when_usernameDoesntExist() {
        assertThat(userRepository.getLoyaltyPoints("jprgic")).isNull();
    }

    @Test
    void getPurchasedBooks_should_returnAllBooksPurchasedByUser() {
        LocalDateTime now = LocalDateTime.now();

        long book1 = insertBook("randomBook1", now.minusYears(1), 100., NEW_RELEASE);
        long book2 = insertBook("randomBook2", now.minusYears(2), 200., REGULAR);
        userRepository.insertNewUser("jprgic");
        insertPurchase("jprgic", book1);
        insertPurchase("jprgic", book2);

        assertThat(userRepository.getPurchasedBooks("jprgic")).extracting("name", "publishedDate", "price")
                                                              .containsExactlyInAnyOrder(tuple("randomBook1", now.minusYears(1).truncatedTo(ChronoUnit.DAYS), BigDecimal.valueOf(50)),
                                                                                         tuple("randomBook2", now.minusYears(2).truncatedTo(ChronoUnit.DAYS), BigDecimal.valueOf(50)));
    }

    private void insertPurchase(String username, long bookId) {
        dslContext.insertInto(PURCHASE).columns(PURCHASE.USER_ID, PURCHASE.BOOK_ID, PURCHASE.PRICE)
                  .values(fetchUserId(username), bookId, BigDecimal.valueOf(50))
                  .execute();
    }

    private long fetchUserId(String username) {
        return dslContext.selectFrom(USER)
                         .where(USER.USERNAME.eq(username))
                         .fetchInto(UserRecord.class).get(0).getId();
    }

    private long insertBook(String name, LocalDateTime dateTime, double price, BookType type) {
        return dslContext.insertInto(BOOK).columns(BOOK.NAME, BOOK.PUBLISHED_DATE, BOOK.TYPE, BOOK.PRICE)
                         .values(name, dateTime, type, BigDecimal.valueOf(price))
                         .returning(BOOK.ID)
                         .fetchOne().getId();
    }

    private List<UserRecord> fetchAllUsers() {
        return dslContext.selectFrom(USER).fetchInto(UserRecord.class);
    }

}