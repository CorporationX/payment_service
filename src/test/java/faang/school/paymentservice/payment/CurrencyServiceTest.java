package faang.school.paymentservice.payment;

import faang.school.paymentservice.config.currency.CurrencyExchangeConfig;
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
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CurrencyServiceTest {

    @InjectMocks
    private CurrencyService currencyService;

    private PaymentRequest payment;

    @BeforeEach
    public void setup() {
        payment = new PaymentRequest(1L,new BigDecimal(12.4), Currency.EUR);
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
        currentCurrencyExchange.setConversion_rates(Currency.USD, BigDecimal.valueOf(1.2));
        currentCurrencyExchange.setConversion_rates(Currency.EUR, BigDecimal.valueOf(0.8));
        BigDecimal expected = BigDecimal.valueOf(150);

        BigDecimal result = currencyService.getAmountInNewCurrency(payment, targetCurrency, currentCurrencyExchange);

        assertEquals(expected, result);
    }
}