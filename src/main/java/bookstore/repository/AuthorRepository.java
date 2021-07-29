package bookstore.repository;

import bookstore.model.Author;
import lombok.RequiredArgsConstructor;
import org.huddle.bookstore.jprgic.tables.records.AuthorRecord;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.huddle.bookstore.jprgic.tables.Author.AUTHOR;
import static org.huddle.bookstore.jprgic.tables.AuthorBook.AUTHOR_BOOK;

@Repository
@RequiredArgsConstructor
public class AuthorRepository {

    private final DSLContext dslContext;

    public List<AuthorRecord> getAuthorsByBookId(long bookId) {
        return dslContext.select()
                         .from(AUTHOR).join(AUTHOR_BOOK).onKey()
                         .where(AUTHOR_BOOK.BOOK_ID.eq(bookId))
                         .fetchInto(AuthorRecord.class);
    }

    public long insertOrGetId(Author author) {
        return dslContext.selectFrom(AUTHOR)
                         .where(AUTHOR.NAME.eq(author.getFirstName()))
                         .and(AUTHOR.LAST_NAME.eq(author.getLastName()))
                         .and(AUTHOR.BIRTH_DATE.eq(author.getDateOfBirth().truncatedTo(ChronoUnit.DAYS)))
                         .fetchOptional(AUTHOR.ID)
                         .orElseGet(() -> dslContext.insertInto(AUTHOR).columns(AUTHOR.NAME, AUTHOR.LAST_NAME, AUTHOR.BIRTH_DATE)
                                                    .values(author.getFirstName(), author.getLastName(), author.getDateOfBirth())
                                                    .returning(AUTHOR.ID)
                                                    .fetchOne().getId());
    }

    public void insertBookAuthor(long bookId, long authorId) {
        dslContext.insertInto(AUTHOR_BOOK).columns(AUTHOR_BOOK.AUTHOR_ID, AUTHOR_BOOK.BOOK_ID)
                  .values(authorId, bookId)
                  .onConflictDoNothing()
                  .execute();
    }

}
