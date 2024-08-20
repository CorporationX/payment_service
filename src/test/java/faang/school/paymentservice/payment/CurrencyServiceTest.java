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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CurrencyServiceTest {

    @InjectMocks
    private CurrencyService currencyService;

    @Mock
    private CurrencyExchangeResponse currentCurrencyExchange;
    private PaymentRequest payment;
    private Currency currency;
    private static final String CONVERTING_MONEY_MESSAGE = "Converting %s %s to %s %s with commission %s";

    @BeforeEach
    public void setup() {
        payment = new PaymentRequest(1L,new BigDecimal(12.4), Currency.EUR);
        currency = Currency.EUR;

        BigDecimal newAmount = new BigDecimal("120");
        BigDecimal newAmountWithCommission = new BigDecimal("124");
        BigDecimal commission = new BigDecimal("4");
    }

    @Test
    public void convertWithComissionTest() {
        when(currentCurrencyExchange.getAmountInNewCurrency(payment, currency))
                .thenReturn(120);
        when(currentCurrencyExchange.addCommission(120))
                .thenReturn(124);

        String expectedMessage = String.format(
                CONVERTING_MONEY_MESSAGE,
                "100.00",
                "USD",
                "124.00",
                "EUR",
                "4.00"
        );

        String result = currencyService.convertWithCommission(payment, currency);

        assertEquals(expectedMessage, result);
    }

    @Test
    public void testAddCommision() {
        BigDecimal amount = BigDecimal.valueOf(100);
        BigDecimal expected = BigDecimal.valueOf(101.5);
        BigDecimal result = currencyService.addCommision(amount);
        assertEquals(expected, result);
    }

    @Test
    public void testGetAmountInNewCurrency() {
        Currency targetCurrency = Currency.EUR;
        CurrencyExchangeResponse currentCurrencyExchange = new CurrencyExchangeResponse();
        currentCurrencyExchange.setConversionRates(Currency.USD, BigDecimal.valueOf(1.2));
        currentCurrencyExchange.setConversionRates(Currency.EUR, BigDecimal.valueOf(0.8));
        BigDecimal expected = BigDecimal.valueOf(150);

        BigDecimal result = currencyService.getAmountInNewCurrency(payment, targetCurrency, currentCurrencyExchange);

        assertEquals(expected, result);
    }
}