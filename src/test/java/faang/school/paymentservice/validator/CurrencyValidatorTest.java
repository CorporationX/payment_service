package faang.school.paymentservice.validator;

import faang.school.paymentservice.dto.Currency;
import faang.school.paymentservice.exception.DataValidationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class CurrencyValidatorTest {

    @Spy
    private CurrencyValidator currencyValidator;

    @Test
    public void testValidatingMinimumTransferAmountIfAmountLessThanMinimum() {
        BigDecimal amountAfterConvert = new BigDecimal("9.00");
        ReflectionTestUtils.setField(currencyValidator, "minimumAmount", new BigDecimal("10"));
        ReflectionTestUtils.setField(currencyValidator, "baseCurrency", Currency.EUR);

        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> currencyValidator.validateMinimumTransferAmount(amountAfterConvert));

        assertEquals("The payment cannot be less than 10EUR", exception.getMessage());
    }

    @Test
    public void testValidatingMinimumTransferAmountIfAmountGreaterThanMinimum() {
        BigDecimal amountAfterConvert = new BigDecimal("12.00");
        ReflectionTestUtils.setField(currencyValidator, "minimumAmount", new BigDecimal("10"));
        ReflectionTestUtils.setField(currencyValidator, "baseCurrency", Currency.EUR);

        boolean result = currencyValidator.validateMinimumTransferAmount(amountAfterConvert);

        assertTrue(result);
    }

    @Test
    public void testValidatingMinimumTransferAmountIfAmountEqualToMinimum() {
        BigDecimal amountAfterConvert = new BigDecimal("10.00");
        ReflectionTestUtils.setField(currencyValidator, "minimumAmount", new BigDecimal("10"));
        ReflectionTestUtils.setField(currencyValidator, "baseCurrency", Currency.EUR);

        boolean result = currencyValidator.validateMinimumTransferAmount(amountAfterConvert);

        assertTrue(result);
    }
}
