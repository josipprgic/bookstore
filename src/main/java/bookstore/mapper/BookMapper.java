package bookstore.mapper;

import bookstore.model.Book;
import bookstore.service.AuthorService;
import lombok.RequiredArgsConstructor;
import org.huddle.bookstore.jprgic.tables.records.BookRecord;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookMapper {

    private final AuthorService authorService;

    public Book map(BookRecord bookRecord) {

        return Book.builder()
                   .name(bookRecord.getName())
                   .publishedDate(bookRecord.getPublishedDate())
                   .bookType(bookRecord.getType())
                   .price(bookRecord.getPrice().doubleValue())
                   .authors(authorService.getAuthorsByBookId(bookRecord.getId()))
                   .build();
    }

}
