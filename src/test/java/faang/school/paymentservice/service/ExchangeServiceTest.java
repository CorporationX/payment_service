package faang.school.paymentservice.service;

import faang.school.paymentservice.client.ExchangeServiceClient;
import faang.school.paymentservice.dto.Currency;
import faang.school.paymentservice.dto.CurrencyRate;
import faang.school.paymentservice.dto.PaymentRequest;
import faang.school.paymentservice.exception.ServiceInteractionException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExchangeServiceTest {

    @Mock
    private ExchangeServiceClient exchangeServiceClient;

    @InjectMocks
    private ExchangeService exchangeService;

    @Test
    void sendPayment_CurrencyNotExist_ThrowsServiceInteractionException() {
        when(exchangeServiceClient.getCurrencyRate(null, Currency.EUR)).thenReturn(getInvalidCurrencyRate());

        assertThrows(ServiceInteractionException.class, () -> exchangeService.sendPayment(getPaymentRequest()));
        verify(exchangeServiceClient, times(1)).getCurrencyRate(null, Currency.EUR);
    }

    @Test
    void sendPayment_ValidArgs() {
        when(exchangeServiceClient.getCurrencyRate(null, Currency.EUR)).thenReturn(getCurrencyRate());
        BigDecimal expected = BigDecimal.valueOf(90.9).setScale(3, RoundingMode.HALF_UP);
        setFee(BigDecimal.valueOf(1.01));

        BigDecimal actual = exchangeService.sendPayment(getPaymentRequest()).amount();

        assertEquals(expected, actual);
        verify(exchangeServiceClient, times(1)).getCurrencyRate(null, Currency.EUR);
    }

    private PaymentRequest getPaymentRequest() {
        return new PaymentRequest(1, BigDecimal.valueOf(100), Currency.EUR);
    }

    private CurrencyRate getCurrencyRate() {
        CurrencyRate currencyRate = new CurrencyRate();
        currencyRate.setRates(Map.of("EUR", BigDecimal.valueOf(0.9)));
        return currencyRate;
    }

    private CurrencyRate getInvalidCurrencyRate() {
        CurrencyRate currencyRate = new CurrencyRate();
        currencyRate.setRates(new HashMap<>());
        return currencyRate;
    }

    private void setFee(BigDecimal value) {
        try {
            Field fee = exchangeService.getClass().getDeclaredField("fee");
            fee.setAccessible(true);
            fee.set(exchangeService, value);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException("Field \"fee\" does not exist", e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Field \"fee\" is not accessible", e);
        }
    }
}
