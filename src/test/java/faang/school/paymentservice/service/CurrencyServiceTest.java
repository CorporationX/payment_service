package faang.school.paymentservice.service;

import faang.school.paymentservice.client.ExternalCurrencyClient;
import faang.school.paymentservice.dto.ExchangeRatesResponse;
import faang.school.paymentservice.service.currency.CurrencyServiceImpl;
import faang.school.paymentservice.store.currencyRate.CurrencyRateStore;
import faang.school.paymentservice.store.currencyRate.CurrencySnapshot;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CurrencyServiceTest {
    String base = "EUR";
    Map<String, Double> rates = Map.of("USD", 1.1);
    CurrencySnapshot updatedSnapshot;
    ExchangeRatesResponse exchangeRatesResponse = new ExchangeRatesResponse(
            "success",
            "EUR",
            Instant.now().toString(),
            rates
    );
    ExchangeRatesResponse exchangeRatesNullResponse = new ExchangeRatesResponse(
            "success",
            "EUR",
            Instant.now().toString(),
            null
    );
    @InjectMocks
    private CurrencyServiceImpl currencyService;

    @Mock
    private ExternalCurrencyClient externalCurrencyClient;

    @Mock
    private CurrencyRateStore currencyRateStore;

    @Captor
    ArgumentCaptor<CurrencySnapshot> snapshotArgumentCaptor;

    @Test
    void testSuccessfullyCurrencyRateUpdate() {
        when(externalCurrencyClient.fetchLatestRates(base)).thenReturn(exchangeRatesResponse);
        currencyService.refreshRates(base);
        verify(externalCurrencyClient, times(1)).fetchLatestRates(eq(base));
        verify(currencyRateStore, times(1)).update(snapshotArgumentCaptor.capture());
        updatedSnapshot = snapshotArgumentCaptor.getValue();
        assertEquals("EUR", updatedSnapshot.base());
        assertEquals(rates, updatedSnapshot.rates());
    }

    @Test
    void testThrowExceptionWhenRatesNull() {
        when(externalCurrencyClient.fetchLatestRates("EUR")).thenReturn(exchangeRatesNullResponse);
        assertThrows(RuntimeException.class,
                () -> currencyService.refreshRates("EUR"));
    }

    @Test
    void testThrowExceptionWhenResponseNull() {
        when(externalCurrencyClient.fetchLatestRates("EUR")).thenReturn(null);
        assertThrows(RuntimeException.class,
                () -> currencyService.refreshRates("EUR"));
    }
}
