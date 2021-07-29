package bookstore.integration;

import bookstore.Application;
import org.jooq.DSLContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.huddle.bookstore.jprgic.tables.Author.AUTHOR;
import static org.huddle.bookstore.jprgic.tables.AuthorBook.AUTHOR_BOOK;
import static org.huddle.bookstore.jprgic.tables.Book.BOOK;
import static org.huddle.bookstore.jprgic.tables.Purchase.PURCHASE;
import static org.huddle.bookstore.jprgic.tables.User.USER;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class IntegrationTestBase {

    @Autowired
    protected DSLContext dslContext;

    @BeforeEach
    void cleanup() {
        dslContext.deleteFrom(PURCHASE).execute();
        dslContext.deleteFrom(AUTHOR_BOOK).execute();
        dslContext.deleteFrom(USER).execute();
        dslContext.deleteFrom(BOOK).execute();
        dslContext.deleteFrom(AUTHOR).execute();
    }
}
