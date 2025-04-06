package faang.school.paymentservice.service.currency;

import faang.school.paymentservice.dto.Currency;
import faang.school.paymentservice.dto.ExchangeRateResponse;
import faang.school.paymentservice.exception.CurrencyRatesUnavailableException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CurrencyServiceTest {

    @Mock
    private WebClient webClient;

    @Mock
    private WebClient.RequestHeadersUriSpec<?> requestHeadersUriSpec;

    @Mock
    private WebClient.RequestHeadersSpec<?> requestHeadersSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    @InjectMocks
    private CurrencyService currencyService;

    @BeforeEach
    public void setUp() throws Exception {
        Field accessKeyField = CurrencyService.class.getDeclaredField("accessKey");
        accessKeyField.setAccessible(true);
        accessKeyField.set(currencyService, "dummy_access_key");
    }

    @Test
    public void fetchCurrencyRatesSuccessTest() {
        ExchangeRateResponse exchangeRateResponse = new ExchangeRateResponse();
        exchangeRateResponse.setSuccess(true);
        exchangeRateResponse.setTimestamp(123456);
        exchangeRateResponse.setBase("EUR");
        exchangeRateResponse.setDate("2023-10-01");

        Map<String, BigDecimal> rates = new HashMap<>();
        rates.put(Currency.USD.name(), new BigDecimal("1.1"));
        rates.put(Currency.EUR.name(), new BigDecimal("1.0"));
        rates.put(Currency.GBP.name(), new BigDecimal("0.9"));
        exchangeRateResponse.setRates(rates);

        doReturn(requestHeadersUriSpec).when(webClient).get();
        when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(ExchangeRateResponse.class)).thenReturn(Mono.just(exchangeRateResponse));

        currencyService.fetchCurrencyRates();

        Map<Currency, BigDecimal> actualRates = currencyService.getCurrencyRates();
        assertEquals(new BigDecimal("1.1"), actualRates.get(Currency.USD));
        assertEquals(new BigDecimal("1.0"), actualRates.get(Currency.EUR));
        assertEquals(new BigDecimal("0.9"), actualRates.get(Currency.GBP));

        verify(webClient).get();
        verify(requestHeadersUriSpec).uri(any(Function.class));
        verify(requestHeadersSpec).retrieve();
        verify(responseSpec).bodyToMono(ExchangeRateResponse.class);
    }

    @Test
    public void fetchCurrencyRatesFailureTest_ResponseIsNull() {
        doReturn(requestHeadersUriSpec).when(webClient).get();
        when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);

        when(responseSpec.bodyToMono(ExchangeRateResponse.class)).thenReturn(Mono.empty());

        CurrencyRatesUnavailableException exception = assertThrows(CurrencyRatesUnavailableException.class,
                () -> currencyService.fetchCurrencyRates());

        assertEquals("Currency rates not available", exception.getMessage());
    }


    @Test
    public void fetchCurrencyRatesFailureTest_SuccessFalse() {
        ExchangeRateResponse exchangeRateResponse = new ExchangeRateResponse();
        exchangeRateResponse.setSuccess(false);
        exchangeRateResponse.setTimestamp(123456);
        exchangeRateResponse.setBase("EUR");
        exchangeRateResponse.setDate("2023-10-01");
        exchangeRateResponse.setRates(new HashMap<>());

        doReturn(requestHeadersUriSpec).when(webClient).get();
        when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(ExchangeRateResponse.class)).thenReturn(Mono.just(exchangeRateResponse));

        CurrencyRatesUnavailableException exception = assertThrows(CurrencyRatesUnavailableException.class,
                () -> currencyService.fetchCurrencyRates());
        assertEquals("Currency rates not available", exception.getMessage());
    }
}