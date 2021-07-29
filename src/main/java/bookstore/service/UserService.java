package bookstore.service;

import bookstore.mapper.BookMapper;
import bookstore.model.Book;
import bookstore.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final BookMapper bookMapper;

    public void insertNewUser(String username) {
        userRepository.insertNewUser(username);
    }

    public Integer getLoyaltyPoints(String username) {
        return userRepository.getLoyaltyPoints(username);
    }

    public List<Book> getPurchasedBooks(String username) {
        return userRepository.getPurchasedBooks(username).stream()
                             .map(bookMapper::map)
                             .collect(Collectors.toUnmodifiableList());
    }

}
