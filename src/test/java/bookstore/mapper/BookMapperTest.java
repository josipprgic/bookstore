package bookstore.mapper;

import bookstore.model.Author;
import bookstore.model.Book;
import bookstore.service.AuthorService;
import org.huddle.bookstore.jprgic.tables.records.BookRecord;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;
import static org.huddle.bookstore.jprgic.enums.BookType.NEW_RELEASE;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookMapperTest {

    @InjectMocks
    private BookMapper bookMapper;

    @Mock
    private AuthorService authorService;

    @Test
    void map_should_correctlyMapBookRecordToBookModel() {
        LocalDateTime now = LocalDateTime.now();

        when(authorService.getAuthorsByBookId(anyLong())).thenReturn(List.of(new Author("josip",
                                                                                        "prgic",
                                                                                        now.minusYears(23))));

        BookRecord bookRecord = new BookRecord()
                .setId(1L)
                .setName("book2")
                .setPublishedDate(now.minusYears(5))
                .setPrice(BigDecimal.valueOf(50))
                .setType(NEW_RELEASE);

        Book book = bookMapper.map(bookRecord);
        assertThat(List.of(book)).extracting("name", "publishedDate", "bookType", "price")
                                              .containsExactly(tuple("book2", now.minusYears(5), NEW_RELEASE, 50.));
        assertThat(book.getAuthors()).extracting("firstName", "lastName", "dateOfBirth")
                                     .containsExactly(tuple("josip", "prgic", now.minusYears(23)));
    }

}