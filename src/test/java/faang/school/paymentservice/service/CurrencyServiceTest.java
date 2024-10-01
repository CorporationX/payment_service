package faang.school.paymentservice.service;

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
    private WebClient webClient;
    private final String baseCurrency = "EUR";
    private CurrencyRateDto dto;
    private Map<Currency, Double> rates;
    private Map<Currency, Double> expectedRates;
    @Captor
    private ArgumentCaptor<Map<Currency, Double>> ratesCaptor;


    @BeforeEach
    public void setup() {
        dto = new CurrencyRateDto("EUR", new HashMap<>(Map.of(Currency.USD, 1.11)));
        rates = new HashMap<>();
        rates.put(Currency.RUB, 100.0);
        rates.put(Currency.USD, 1.1);
        expectedRates = new HashMap<>();
        expectedRates.put(Currency.RUB, 100.0);
        expectedRates.put(Currency.USD, 1.1);
    }


    @Test
    public void testGetCurrencyRates() {
        // Arrange
        when(currencyRateCache.getAllCurrencyRates()).thenReturn(rates);

        // Act
        Map<Currency, Double> returnRates = service.getAllCurrencyRates();

        // Assert
        Assertions.assertEquals(expectedRates, returnRates);
    }

    @Test
    public void testUpdateActualCurrencyRate_EmptyResponse() {
        // Arrange
        when(currencyRateCache.getBaseCurrency()).thenReturn(baseCurrency);
        when(webClient.get()
                .uri((URI) any())
                .retrieve()
                .bodyToMono(CurrencyRateDto.class)
                .retryWhen(Retry.fixedDelay(3, Duration.ofSeconds(1)))
                .block()
        ).thenReturn(null);

        // Act & Assert
        Exception exception = Assertions.assertThrows(NullPointerException.class,() -> service.updateActualCurrencyRate());
        Assertions.assertEquals("Получен пустой ответ от сервиса курса валют.Обновление курса валют не выполнено",
                exception.getMessage());
    }

    @Test
    public void testUpdateActualCurrencyRate_EmptyRates() {
        // Arrange
        when(currencyRateCache.getBaseCurrency()).thenReturn(baseCurrency);
        when(webClient.get()
                .uri((URI) any())
                .retrieve()
                .bodyToMono(CurrencyRateDto.class)
                .retryWhen(Retry.fixedDelay(3, Duration.ofSeconds(1)))
                .block()
        ).thenReturn(new CurrencyRateDto("EUR", null));

        // Act & Assert
        Exception exception = Assertions.assertThrows(NullPointerException.class,() -> service.updateActualCurrencyRate());
        Assertions.assertEquals("Получен пустой список курса от сервиса курса валют.Обновление курса валют не выполнено",
                exception.getMessage());
    }

    @Test
    public void testUpdateActualCurrencyRate() {
        // Arrange
        when(currencyRateCache.getBaseCurrency()).thenReturn(baseCurrency);
        when(webClient.get()
                .uri((URI) any())
                .retrieve()
                .bodyToMono(CurrencyRateDto.class)
                .retryWhen(Retry.fixedDelay(3, Duration.ofSeconds(1)))
                .block()
        ).thenReturn(new CurrencyRateDto("EUR", null));

        // Act & Assert
        service.updateActualCurrencyRate();
        verify(currencyRateCache, times(1)).setCurrencyRate(ratesCaptor.capture());
        Assertions.assertEquals(expectedRates, ratesCaptor.getValue());
    }
}
