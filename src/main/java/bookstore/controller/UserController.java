package bookstore.controller;

import bookstore.model.Book;
import bookstore.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("insert")
    public void createNewUser(@RequestParam @NotNull String username) {
        userService.insertNewUser(username);
    }

    @GetMapping("loyaltyPoints")
    public Integer getLoyaltyPoints(@RequestParam @NotNull String username) {
        return userService.getLoyaltyPoints(username);
    }

    @GetMapping("purchases")
    public List<Book> getPurchasedBooks(@RequestParam @NotNull String username) {
        return userService.getPurchasedBooks(username);
    }
}
