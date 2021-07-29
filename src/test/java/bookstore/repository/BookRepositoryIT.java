package bookstore.repository;

import bookstore.integration.IntegrationTestBase;
import bookstore.model.Book;
import org.huddle.bookstore.jprgic.tables.records.BookRecord;
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
import static org.huddle.bookstore.jprgic.enums.BookType.OLD_EDITION;
import static org.huddle.bookstore.jprgic.enums.BookType.REGULAR;
import static org.huddle.bookstore.jprgic.tables.Book.BOOK;
import static org.huddle.bookstore.jprgic.tables.User.USER;

class BookRepositoryIT extends IntegrationTestBase {

    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private UserRepository userRepository;

    @Test
    void insertBook_should_insertNewBook() {
        LocalDateTime now = LocalDateTime.now();
        bookRepository.insertBook(Book.builder().name("book1").publishedDate(now.minusYears(2)).price(50.).bookType(NEW_RELEASE).build());

        assertThat(fetchAllBooks()).extracting("name", "publishedDate", "price", "type")
                                   .containsExactly(tuple("book1",
                                                          now.minusYears(2).truncatedTo(ChronoUnit.DAYS),
                                                          BigDecimal.valueOf(50.),
                                                          NEW_RELEASE));
    }

    @Test
    void insertBook_should_throw_when_insertingDuplicateBook() {
        LocalDateTime now = LocalDateTime.now();
        bookRepository.insertBook(Book.builder().name("book1").publishedDate(now.minusYears(2)).price(50.).bookType(NEW_RELEASE).build());

        assertThatThrownBy(() -> bookRepository.insertBook(Book.builder()
                                                               .name("book1")
                                                               .publishedDate(now.minusYears(2))
                                                               .price(50.)
                                                               .bookType(NEW_RELEASE)
                                                               .build())).isExactlyInstanceOf(
                DuplicateKeyException.class);
    }

    @Test
    void getAllBooks_should_returnAllBoksInDB() {
        LocalDateTime now = LocalDateTime.now();
        bookRepository.insertBook(Book.builder().name("book1").publishedDate(now.minusYears(2)).price(50.).bookType(NEW_RELEASE).build());
        bookRepository.insertBook(Book.builder().name("book2").publishedDate(now.minusYears(3)).price(25.).bookType(OLD_EDITION).build());

        assertThat(bookRepository.getAllBookRecords()).extracting("name", "publishedDate", "price", "type")
                                                      .containsExactlyInAnyOrder(tuple("book1",
                                                                                       now.minusYears(2).truncatedTo(ChronoUnit.DAYS),
                                                                                       BigDecimal.valueOf(50.),
                                                                                       NEW_RELEASE),
                                                                                 tuple("book2",
                                                                                       now.minusYears(3).truncatedTo(ChronoUnit.DAYS),
                                                                                       BigDecimal.valueOf(25.),
                                                                                       OLD_EDITION));
    }

    @Test
    void processBooksPurchase_should_throw_when_userDoesntExist() {
        LocalDateTime now = LocalDateTime.now();
        Book book = Book.builder().name("book1").publishedDate(now.minusYears(2)).price(50.).bookType(NEW_RELEASE).build();
        bookRepository.insertBook(book);

        assertThatThrownBy(() -> bookRepository.processBooksPurchase("jprgic", List.of(book), true)).isInstanceOf(RuntimeException.class);

        assertThat(userRepository.getPurchasedBooks("jprgic")).isEmpty();
    }

    @Test
    void processBooksPurchase_should_throw_when_bookDoesntExist() {
        LocalDateTime now = LocalDateTime.now();
        Book book = Book.builder().name("book1").publishedDate(now.minusYears(2)).price(50.).bookType(NEW_RELEASE).build();
        userRepository.insertNewUser("jprgic");
        updateUserLoyaltyPoints("jprgic", 10);

        assertThatThrownBy(() -> bookRepository.processBooksPurchase("jprgic", List.of(book), true)).isInstanceOf(RuntimeException.class);

        assertThat(userRepository.getPurchasedBooks("jprgic")).isEmpty();
    }

    @Test
    void processBooksPurchase_should_buyBookForLoyaltyPoints_when_userHasEnoughPoints() {
        LocalDateTime now = LocalDateTime.now();
        Book book = Book.builder().name("book1").publishedDate(now.minusYears(2)).price(50.).bookType(NEW_RELEASE).build();
        userRepository.insertNewUser("jprgic");
        bookRepository.insertBook(book);
        updateUserLoyaltyPoints("jprgic", 10);

        bookRepository.processBooksPurchase("jprgic", List.of(book), true);
        assertThat(userRepository.getPurchasedBooks("jprgic")).extracting("name", "price")
                                                              .containsExactly(tuple("book1", BigDecimal.valueOf(0)));
    }

    @Test
    void processBooksPurchase_should_throw_when_userDoesntHaveEnoughPoints() {
        LocalDateTime now = LocalDateTime.now();
        Book book = Book.builder().name("book1").publishedDate(now.minusYears(2)).price(50.).bookType(NEW_RELEASE).build();
        userRepository.insertNewUser("jprgic");
        bookRepository.insertBook(book);

        assertThatThrownBy(() -> bookRepository.processBooksPurchase("jprgic", List.of(book), true)).isInstanceOf(RuntimeException.class);

        assertThat(userRepository.getPurchasedBooks("jprgic")).isEmpty();
    }

    @Test
    void processBooksPurchase_should_notBuyAnyBooksForUsersLoyaltyPoints_when_userDoesntHaveEnoughPointsForAllBooks() {
        LocalDateTime now = LocalDateTime.now();
        Book book1 = Book.builder().name("book1").publishedDate(now.minusYears(2)).price(50.).bookType(NEW_RELEASE).build();
        Book book2 = Book.builder().name("book2").publishedDate(now.minusYears(5)).price(25.).bookType(OLD_EDITION).build();
        userRepository.insertNewUser("jprgic");
        bookRepository.insertBook(book1);
        bookRepository.insertBook(book2);
        updateUserLoyaltyPoints("jprgic", 10);

        assertThatThrownBy(() -> bookRepository.processBooksPurchase("jprgic",
                                                                     List.of(book1, book2),
                                                                     true)).isInstanceOf(RuntimeException.class);

        assertThat(userRepository.getPurchasedBooks("jprgic")).isEmpty();
    }

    @Test
    void processBooksPurchase_should_applyDiscountsAndUpdateLoyaltyPoints_when_buyingRegularly() {
        LocalDateTime now = LocalDateTime.now();
        Book book1 = Book.builder().name("book1").publishedDate(now.minusYears(2)).price(100.).bookType(NEW_RELEASE).build();
        Book book2 = Book.builder().name("book2").publishedDate(now.minusYears(5)).price(100.).bookType(REGULAR).build();
        Book book3 = Book.builder().name("book3").publishedDate(now.minusYears(10)).price(100.).bookType(OLD_EDITION).build();
        userRepository.insertNewUser("jprgic");
        bookRepository.insertBook(book1);
        bookRepository.insertBook(book2);
        bookRepository.insertBook(book3);

        bookRepository.processBooksPurchase("jprgic", List.of(book1, book2), false);
        bookRepository.processBooksPurchase("jprgic", List.of(book3), false);

        assertThat(userRepository.getPurchasedBooks("jprgic")).extracting("name", "price")
                                                              .containsExactlyInAnyOrder(tuple("book1", new BigDecimal("100.00")),
                                                                                         tuple("book2", new BigDecimal("100.00")),
                                                                                         tuple("book3", new BigDecimal("80.00")));
        assertThat(userRepository.getLoyaltyPoints("jprgic")).isEqualTo(3);
    }

    @Test
    void processBooksPurchase_should_applyDiscountsAndUpdateLoyaltyPoints_when_buyingRegularlyInBulk() {
        LocalDateTime now = LocalDateTime.now();
        Book book1 = Book.builder().name("book1").publishedDate(now.minusYears(2)).price(100.).bookType(NEW_RELEASE).build();
        Book book2 = Book.builder().name("book2").publishedDate(now.minusYears(5)).price(100.).bookType(REGULAR).build();
        Book book3 = Book.builder().name("book3").publishedDate(now.minusYears(10)).price(100.).bookType(OLD_EDITION).build();
        userRepository.insertNewUser("jprgic");
        bookRepository.insertBook(book1);
        bookRepository.insertBook(book2);
        bookRepository.insertBook(book3);

        bookRepository.processBooksPurchase("jprgic", List.of(book1, book2, book3), false);

        assertThat(userRepository.getPurchasedBooks("jprgic")).extracting("name", "price")
                                                              .containsExactlyInAnyOrder(tuple("book1", new BigDecimal("100.00")),
                                                                                         tuple("book2", new BigDecimal("90.00")),
                                                                                         tuple("book3", new BigDecimal("75.000")));
        assertThat(userRepository.getLoyaltyPoints("jprgic")).isEqualTo(3);
    }

    private void updateUserLoyaltyPoints(String username, int points) {
        dslContext.update(USER)
                  .set(USER.LOYALTY_POINTS, points)
                  .execute();
    }

    private List<BookRecord> fetchAllBooks() {
        return dslContext.selectFrom(BOOK)
                         .fetchInto(BookRecord.class);
    }

}