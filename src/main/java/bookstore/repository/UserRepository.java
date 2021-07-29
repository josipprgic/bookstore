package bookstore.repository;

import lombok.RequiredArgsConstructor;
import org.huddle.bookstore.jprgic.tables.records.BookRecord;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.List;

import static org.huddle.bookstore.jprgic.tables.Book.BOOK;
import static org.huddle.bookstore.jprgic.tables.Purchase.PURCHASE;
import static org.huddle.bookstore.jprgic.tables.User.USER;

@Repository
@RequiredArgsConstructor
public class UserRepository {

    private final DSLContext dslContext;

    public void insertNewUser(String username) {
        dslContext.insertInto(USER).columns(USER.USERNAME)
                  .values(username)
                  .execute();
    }

    public Integer getLoyaltyPoints(String username) {
        return dslContext.selectFrom(USER)
                         .where(USER.USERNAME.eq(username))
                         .fetchOne(USER.LOYALTY_POINTS);
    }

    public List<BookRecord> getPurchasedBooks(String username) {
        return dslContext.select(BOOK.ID, BOOK.NAME, BOOK.PUBLISHED_DATE, BOOK.TYPE, PURCHASE.PRICE)
                         .from(BOOK).join(PURCHASE).onKey().join(USER).onKey()
                         .where(USER.USERNAME.eq(username))
                         .fetchInto(BookRecord.class);
    }

}
