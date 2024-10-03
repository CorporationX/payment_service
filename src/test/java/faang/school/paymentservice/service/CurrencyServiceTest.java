package faang.school.paymentservice.service;

import faang.school.paymentservice.client.CurrencyClient;
import faang.school.paymentservice.dto.Currency;
import faang.school.paymentservice.dto.CurrencyRateDto;
import faang.school.paymentservice.service.currency.CurrencyRateCache;
import faang.school.paymentservice.service.currency.CurrencyService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.util.retry.Retry;

import java.net.URI;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CurrencyServiceTest {

    @InjectMocks
    private CurrencyService service;

    @Mock
    private CurrencyRateCache currencyRateCache;

    @Mock
    private CurrencyClient client;
    private final String baseCurrency = "EUR";
    private final String date = "2022-01-01";
    private CurrencyRateDto dto;
    private Map<Currency, Double> rates;
    private Map<Currency, Double> expectedRates;
    private CurrencyRateDto expectedDto;

    @Captor
    private ArgumentCaptor<Map<Currency, Double>> ratesCaptor;


    @BeforeEach
    public void setup() {
        rates = new HashMap<>();
        rates.put(Currency.RUB, 100.0);
        rates.put(Currency.USD, 1.1);
        expectedRates = new HashMap<>();
        expectedRates.put(Currency.RUB, 100.0);
        expectedRates.put(Currency.USD, 1.1);
        dto = new CurrencyRateDto(date, baseCurrency, rates);
        expectedDto = new CurrencyRateDto(date, baseCurrency, expectedRates);
    }


    @Test
    public void testCheckHealth() {
        // Arrange
        when(currencyRateCache.getAllCurrencyRates()).thenReturn(rates);
        when(currencyRateCache.getDate()).thenReturn(date);
        when(currencyRateCache.getBaseCurrency()).thenReturn(baseCurrency);

        // Act
        CurrencyRateDto returnDto = service.checkHealth();

        // Assert
        Assertions.assertEquals(expectedDto, returnDto);
    }

    @Test
    public void testUpdateActualCurrencyRate_EmptyResponse() {
        // Arrange
        when(currencyRateCache.getBaseCurrency()).thenReturn(baseCurrency);
        when(client.getCurrencyRates(any(), any())).thenReturn(null);

        // Act & Assert
        Exception exception = Assertions.assertThrows(NullPointerException.class,() -> service.updateActualCurrencyRate());
        Assertions.assertEquals("An empty response was received from the exchange rate service.Currency exchange rate update failed",
                exception.getMessage());
    }

    @Test
    public void testUpdateActualCurrencyRate_EmptyRates() {
        // Arrange
        when(currencyRateCache.getBaseCurrency()).thenReturn(baseCurrency);
        when(client.getCurrencyRates(any(), any()))
                .thenReturn(new CurrencyRateDto("2022-01-01", "EUR", null ));

        // Act & Assert
        Exception exception = Assertions.assertThrows(NullPointerException.class,() -> service.updateActualCurrencyRate());
        Assertions.assertEquals("An empty list of the exchange rate was received from the exchange rate service.Currency exchange rate update failed",
                exception.getMessage());
    }

    @Test
    public void testUpdateActualCurrencyRate() {
        // Arrange
        when(currencyRateCache.getBaseCurrency()).thenReturn(baseCurrency);
        when(client.getCurrencyRates(any(), any()))
                .thenReturn(dto);

        // Act & Assert
        service.updateActualCurrencyRate();
        verify(currencyRateCache, times(1)).setCurrencyRate(ratesCaptor.capture());
        Assertions.assertEquals(expectedRates, ratesCaptor.getValue());
    }
}
