package faang.school.paymentservice.payment;

import faang.school.paymentservice.dto.Currency;
import faang.school.paymentservice.dto.PaymentRequest;
import faang.school.paymentservice.dto.exchange.CurrencyExchangeResponse;
import faang.school.paymentservice.service.CurrencyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.ReflectionUtils;

@ExtendWith(MockitoExtension.class)
public class CurrencyServiceTest {

    @InjectMocks
    private CurrencyService currencyService;
    @Mock
    private CurrencyExchangeResponse currentCurrencyExchange;
    private PaymentRequest payment;
    private Currency currency;
    private static final String CONVERTING_MONEY_MESSAGE = "Dear friend! Thank you for converting money! You converted %s %s to %s %s with commission %f%%";

    @BeforeEach
    public void setup() {
        payment = new PaymentRequest(1L, new BigDecimal(12.4), Currency.EUR);
        currency = Currency.EUR;

        currentCurrencyExchange.setConversionRates(Currency.USD, BigDecimal.valueOf(1.2));
        currentCurrencyExchange.setConversionRates(Currency.EUR, BigDecimal.valueOf(0.8));
    }

    @Test
    public void convertWithComissionTest() {
        BigDecimal rate = BigDecimal.valueOf(1.2);

        ReflectionTestUtils.setField(currencyService, "currentCurrencyExchange", currentCurrencyExchange);

        ReflectionTestUtils.setField(currencyService, "commission", Double.valueOf("2.1"));
        when(currentCurrencyExchange.getRate(currency)).thenReturn(rate);
        when(currentCurrencyExchange.getRate(payment.currency())).thenReturn(rate);


        String expectedMessage = String.format(
                CONVERTING_MONEY_MESSAGE,
                "12,40",
                "EUR",
                "12,66",
                "EUR",
                Double.valueOf("2.1")
        );

        String result = currencyService.convertWithCommission(payment, currency);
        assertEquals(expectedMessage, result);
    }
}