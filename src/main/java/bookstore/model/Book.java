package bookstore.model;

import lombok.Builder;
import lombok.Value;
import org.huddle.bookstore.jprgic.enums.BookType;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@Value
@Builder
public class Book {
    @NotNull String name;
    @NotNull LocalDateTime publishedDate;
    Double price;
    BookType bookType;
    List<Author> authors;
}
