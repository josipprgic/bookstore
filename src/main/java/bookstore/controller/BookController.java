package bookstore.controller;

import bookstore.model.Book;
import bookstore.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequestMapping("/book")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    @GetMapping("all")
    public List<Book> getBooks() {
        return bookService.getAllBooks();
    }

    @PostMapping("insert")
    public void insertBook(@RequestBody @NotNull @Valid Book book) {
        bookService.insertBook(book);
    }

    @PutMapping("buy")
    public void buyBook(@RequestParam @NotNull String username, @RequestBody @NotNull @Valid Book book, @RequestParam boolean useLoyaltyPoints) {
        bookService.buyBooks(username, List.of(book), useLoyaltyPoints);
    }

    @PutMapping("buyBulk")
    public void buyBookInBulk(@RequestParam @NotNull String username, @RequestBody @NotNull @Valid List<Book> books, @RequestParam boolean useLoyaltyPoints) {
        bookService.buyBooks(username, books, useLoyaltyPoints);
    }
}
