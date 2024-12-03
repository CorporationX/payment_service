package faang.school.paymentservice.service.payment;

import faang.school.paymentservice.dto.Currency;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class PaymentMessageServiceTest {

    @Spy
    private PaymentMessageService paymentMessageService;
    private String formattedSum;
    private String formattedSumInBaseCurrency;
    private Currency baseCurrency;

    @BeforeEach
    public void setUp() {
        baseCurrency = Currency.EUR;
        formattedSum = "100.00";
    }

    @Test
    public void testGenerateMessageAfterPaymentIfPaymentCurrencyIsEuro() {
        String expectedMessage = "Dear friend! Thank you for your purchase! " +
                "Your payment on 100.00 EUR was accepted.";
        formattedSumInBaseCurrency = "100.00";

        String actualMessage = paymentMessageService.generateMessageAfterPayment(formattedSum, Currency.EUR, formattedSumInBaseCurrency, baseCurrency);

        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    public void testGenerateMessageAfterPaymentIfPaymentCurrencyNotEuro() {
        String expectedMessage = "Dear friend! Thank you for your purchase! " +
                "Your payment on 100.00 AUD (61.72 EUR) was accepted.";
        formattedSumInBaseCurrency = "61.72";

        String actualMessage = paymentMessageService.generateMessageAfterPayment(formattedSum, Currency.AUD, formattedSumInBaseCurrency, baseCurrency);

        assertEquals(expectedMessage, actualMessage);
    }
}
