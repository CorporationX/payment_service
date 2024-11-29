package faang.school.paymentservice.service.currency;

import faang.school.paymentservice.dto.RatesDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CurrencyRateFetcherTest {

    @Mock
    private WebClient webClient;

    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    @InjectMocks
    private CurrencyRateFetcher currencyRateFetcher;

    @Value("${services.exchange.endpoint}")
    String endpoint;

    @Value("${services.exchange.key}")
    String key;

    @Test
    void fetchDataTest() {
        Map<String, Double> rates = new HashMap<>();
        rates.put("RUB", 100.0);
        RatesDto mockRatesDto = new RatesDto();
        mockRatesDto.setRates(rates);
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(endpoint + key)).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(RatesDto.class)).thenReturn(Mono.just(mockRatesDto));

        RatesDto result = currencyRateFetcher.fetchData();

        assertEquals(mockRatesDto, result);
        verify(webClient, times(1)).get();
    }
}
