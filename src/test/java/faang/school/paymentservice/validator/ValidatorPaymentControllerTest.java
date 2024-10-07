package faang.school.paymentservice.validator;

import faang.school.paymentservice.model.enums.Currency;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ValidatorPaymentControllerTest {
    private ValidatorPaymentController validatorPaymentController = new ValidatorPaymentController();

    @Test
    void checkCurrencySuccess() {
        validatorPaymentController.checkCurrency(Currency.USD);
        Assertions.assertDoesNotThrow(() -> validatorPaymentController.checkCurrency(Currency.USD));
    }

    @Test
    void checkCurrencyUnSuccess() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            validatorPaymentController.checkCurrency(Currency.valueOf("YYY"));
        });
    }
}