package bookstore.discount;

import org.huddle.bookstore.jprgic.enums.BookType;
import org.springframework.stereotype.Service;

import java.util.Map;

import static org.huddle.bookstore.jprgic.enums.BookType.NEW_RELEASE;
import static org.huddle.bookstore.jprgic.enums.BookType.OLD_EDITION;
import static org.huddle.bookstore.jprgic.enums.BookType.REGULAR;

@Service
public class DiscountSupplier {

    private static final int MIN_BULK_SIZE_TO_APPLY_DISCOUNT = 3;
    private static final Map<BookType, BookPurchaseDiscount> discountsByType = Map.of(NEW_RELEASE, new BookPurchaseDiscount(0., 0.),
                                                                               REGULAR, new BookPurchaseDiscount(0., 0.1),
                                                                               OLD_EDITION, new BookPurchaseDiscount(0.2, 0.05));

    public double getTotalDiscount(BookType bookType, int bulkSize) {
        BookPurchaseDiscount discount = discountsByType.get(bookType);

        return discount.getPercentage() + (bulkSize >= MIN_BULK_SIZE_TO_APPLY_DISCOUNT ? discount.getBulkPercentage() : 0.);
    }
}
