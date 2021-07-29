package bookstore.model;

import lombok.Value;
import org.huddle.bookstore.jprgic.tables.records.AuthorRecord;

import java.time.LocalDateTime;

@Value
public class Author {

    String firstName;
    String lastName;
    LocalDateTime dateOfBirth;

    public static Author from(AuthorRecord authorRecord) {
        return new Author(authorRecord.getName(), authorRecord.getLastName(), authorRecord.getBirthDate());
    }

}
