package bookstore.repository;

import bookstore.discount.DiscountSupplier;
import bookstore.model.Book;
import lombok.RequiredArgsConstructor;
import org.huddle.bookstore.jprgic.Routines;
import org.huddle.bookstore.jprgic.enums.BookType;
import org.huddle.bookstore.jprgic.tables.records.BookRecord;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.huddle.bookstore.jprgic.tables.Book.BOOK;

@Repository
@RequiredArgsConstructor
public class BookRepository {

    private final DSLContext dslContext;
    private final DiscountSupplier discountSupplier;

    public List<BookRecord> getAllBookRecords() {
        return dslContext.selectFrom(BOOK)
                         .fetchInto(BookRecord.class);
    }

    public long insertBook(Book book) {
        return dslContext.insertInto(BOOK).columns(BOOK.NAME, BOOK.PUBLISHED_DATE, BOOK.TYPE, BOOK.PRICE)
                         .values(book.getName(), book.getPublishedDate(), book.getBookType(), BigDecimal.valueOf(book.getPrice()))
                         .returning(BOOK.ID)
                         .fetchOne().getId();
    }

    @Transactional
    public void processBooksPurchase(String username, List<Book> books, boolean useLoyaltyPoints) {
        if (useLoyaltyPoints) {
            books.forEach(book -> processLoyaltyPurchase(username, book));
            return;
        }

        books.forEach(book -> processBookPurchase(username, book, discountSupplier.getTotalDiscount(fetchBookType(book), books.size())));
    }

    private BookType fetchBookType(Book book) {
        return dslContext.selectFrom(BOOK)
                .where(BOOK.NAME.eq(book.getName())).and(BOOK.PUBLISHED_DATE.eq(book.getPublishedDate().truncatedTo(ChronoUnit.DAYS)))
                .fetchOne(BOOK.TYPE);
    }

    private void processLoyaltyPurchase(String username, Book book) {
        Routines.purchaseBook(dslContext.configuration(), username, book.getName(), book.getPublishedDate(), true, BigDecimal.valueOf(0));
    }

    private void processBookPurchase(String username, Book book, double discount) {
        Routines.purchaseBook(dslContext.configuration(), username, book.getName(), book.getPublishedDate(), false, BigDecimal.valueOf(discount));
    }

}
