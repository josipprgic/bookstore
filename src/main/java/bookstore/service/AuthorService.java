package bookstore.service;

import bookstore.model.Author;
import bookstore.repository.AuthorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthorService {

    private final AuthorRepository authorRepository;

    public List<Author> getAuthorsByBookId(long bookId) {
        return authorRepository.getAuthorsByBookId(bookId).stream()
                .map(Author::from)
                .collect(Collectors.toUnmodifiableList());
    }

    public void insertBookAuthor(long bookId, Author author) {
        long authorId = authorRepository.insertOrGetId(author);

        authorRepository.insertBookAuthor(bookId, authorId);
    }

}
