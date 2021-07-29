package bookstore.repository;

import bookstore.integration.IntegrationTestBase;
import bookstore.model.Author;
import bookstore.model.Book;
import org.huddle.bookstore.jprgic.tables.records.AuthorRecord;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;
import static org.huddle.bookstore.jprgic.enums.BookType.NEW_RELEASE;
import static org.huddle.bookstore.jprgic.tables.Author.AUTHOR;

class AuthorRepositoryIT extends IntegrationTestBase {

    @Autowired
    private AuthorRepository authorRepository;
    @Autowired
    private BookRepository bookRepository;

    @Test
    void insertOrGetId_should_insertNewAuthor_when_authorDoesntExist() {
        LocalDateTime now = LocalDateTime.now();

        long authorId = authorRepository.insertOrGetId(new Author("josip", "prgic", now.minusYears(25)));

        assertThat(fetchAllAuthors()).extracting("id", "name", "lastName", "birthDate")
                                     .containsExactly(tuple(authorId, "josip", "prgic", now.minusYears(25).truncatedTo(ChronoUnit.DAYS)));
    }

    @Test
    void insertOrGetId_should_returnId_when_authorAlreadyExist() {
        LocalDateTime now = LocalDateTime.now();

        authorRepository.insertOrGetId(new Author("josip", "prgic", now.minusYears(25)));
        long authorId = authorRepository.insertOrGetId(new Author("josip", "prgic", now.minusYears(25)));

        assertThat(fetchAllAuthors()).extracting("id", "name", "lastName", "birthDate")
                                     .containsExactly(tuple(authorId, "josip", "prgic", now.minusYears(25).truncatedTo(ChronoUnit.DAYS)));
    }

    @Test
    void getAuthorsByBookId_should_returnAllAuthorsForSomeBooke() {
        LocalDateTime now = LocalDateTime.now();
        Book book = Book.builder().name("book1").publishedDate(now.minusYears(2)).price(100.).bookType(NEW_RELEASE).build();
        long author1 = authorRepository.insertOrGetId(new Author("josip", "prgic", now.minusYears(25)));
        long author2 = authorRepository.insertOrGetId(new Author("ivo", "ivic", now.minusYears(37)));
        long bookId = bookRepository.insertBook(book);

        authorRepository.insertBookAuthor(bookId, author1);
        authorRepository.insertBookAuthor(bookId, author2);

        assertThat(authorRepository.getAuthorsByBookId(bookId)).extracting("id", "name", "lastName", "birthDate")
                                                               .containsExactlyInAnyOrder(tuple(author1, "josip", "prgic", now.minusYears(25).truncatedTo(ChronoUnit.DAYS)),
                                                                                          tuple(author2, "ivo", "ivic", now.minusYears(37).truncatedTo(ChronoUnit.DAYS)));
    }

    private List<AuthorRecord> fetchAllAuthors() {
        return dslContext.selectFrom(AUTHOR)
                         .fetchInto(AuthorRecord.class);
    }

}