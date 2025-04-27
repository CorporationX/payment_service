package faang.school.paymentservice.service.currency;

import faang.school.paymentservice.dto.CurrencyDto;
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

import java.math.BigDecimal;
import java.util.Map;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CurrencyServiceTest {

    @Mock
    private WebClient webClient;

    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    @InjectMocks
    private CurrencyService currencyService;

    @BeforeEach
    void setUp() {
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);
    }

    @Test
    void fetchCurrencyRates_SuccessfulResponse_UpdatesRates() {
        ExchangeRateResponse mockResponse = new ExchangeRateResponse();
        mockResponse.setSuccess(true);
        mockResponse.setRates(Map.of(
                "USD", BigDecimal.valueOf(1.2),
                "EUR", BigDecimal.valueOf(1.0),
                "GBP", BigDecimal.valueOf(0.9)
        ));

        when(responseSpec.bodyToMono(ExchangeRateResponse.class))
                .thenReturn(Mono.just(mockResponse));

        currencyService.fetchCurrencyRates().block();

        Map<CurrencyDto, BigDecimal> rates = currencyService.getCurrencyRates();
        assertEquals(BigDecimal.valueOf(1.2), rates.get(CurrencyDto.USD));
        assertEquals(BigDecimal.valueOf(1.0), rates.get(CurrencyDto.EUR));
        assertEquals(BigDecimal.valueOf(0.9), rates.get(CurrencyDto.GBP));
    }

    @Test
    void fetchCurrencyRates_EmptyResponse_ThrowsException() {
        when(responseSpec.bodyToMono(ExchangeRateResponse.class))
                .thenReturn(Mono.empty());

        Exception exception = assertThrows(CurrencyRatesUnavailableException.class, () -> {
            currencyService.fetchCurrencyRates().block();
        });

        assertTrue(exception.getMessage().contains("Currency rates not available"));
    }

    @Test
    void getCurrencyRates_ReturnsUnmodifiableMap() {
        ExchangeRateResponse mockResponse = new ExchangeRateResponse();
        mockResponse.setSuccess(true);
        mockResponse.setRates(Map.of(
                "USD", BigDecimal.valueOf(1.2),
                "EUR", BigDecimal.valueOf(1.0),
                "GBP", BigDecimal.valueOf(0.9)
        ));

        when(responseSpec.bodyToMono(ExchangeRateResponse.class))
                .thenReturn(Mono.just(mockResponse));

        currencyService.fetchCurrencyRates().block();

        Map<CurrencyDto, BigDecimal> rates = currencyService.getCurrencyRates();

        assertThrows(UnsupportedOperationException.class, () -> {
            rates.put(CurrencyDto.USD, BigDecimal.ZERO);
        });
    }
}