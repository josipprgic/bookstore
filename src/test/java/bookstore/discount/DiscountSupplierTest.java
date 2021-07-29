package bookstore.discount;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.huddle.bookstore.jprgic.enums.BookType.NEW_RELEASE;
import static org.huddle.bookstore.jprgic.enums.BookType.OLD_EDITION;
import static org.huddle.bookstore.jprgic.enums.BookType.REGULAR;

class DiscountSupplierTest {

    private final DiscountSupplier discountSupplier = new DiscountSupplier();

    @ParameterizedTest
    @ValueSource(ints = {0, 3, 100})
    void getTotalDiscount_should_returnZero_when_bookTypeIsNewRelease(int bulkSize) {
        assertThat(discountSupplier.getTotalDiscount(NEW_RELEASE, bulkSize)).isZero();
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 2})
    void getTotalDiscount_should_returnZero_when_bookTypeIsRegularAndBulkCountLTThree(int bulkSize) {
        assertThat(discountSupplier.getTotalDiscount(REGULAR, bulkSize)).isZero();
    }

    @ParameterizedTest
    @ValueSource(ints = {3, 5, 100})
    void getTotalDiscount_should_returnTenPercent_when_bookTypeIsRegularAndBulkCountGETThree(int bulkSize) {
        assertThat(discountSupplier.getTotalDiscount(REGULAR, bulkSize)).isEqualTo(0.1);
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 2})
    void getTotalDiscount_should_returnTwentyPercent_when_bookTypeIsOldEditionAndBulkCountLTThree(int bulkSize) {
        assertThat(discountSupplier.getTotalDiscount(OLD_EDITION, bulkSize)).isEqualTo(0.2);
    }

    @ParameterizedTest
    @ValueSource(ints = {3, 4, 40})
    void getTotalDiscount_should_returnTwentyFivePercent_when_bookTypeIsOldEditionAndBulkCountGETThree(int bulkSize) {
        assertThat(discountSupplier.getTotalDiscount(OLD_EDITION, bulkSize)).isEqualTo(0.25);
    }
}