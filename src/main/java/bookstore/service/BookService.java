package bookstore.service;

import bookstore.mapper.BookMapper;
import bookstore.model.Book;
import bookstore.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;
    private final AuthorService authorService;
    private final BookMapper bookMapper;

    public List<Book> getAllBooks() {
        return bookRepository.getAllBookRecords().stream()
                             .map(bookMapper::map)
                             .collect(Collectors.toUnmodifiableList());
    }

    public void insertBook(Book book) {
        long bookId = bookRepository.insertBook(book);

        book.getAuthors()
            .forEach(author -> authorService.insertBookAuthor(bookId, author));
    }

    public void buyBooks(String username, List<Book> books, boolean usingLoyaltyPoints) {
        bookRepository.processBooksPurchase(username, books, usingLoyaltyPoints);
    }

}
