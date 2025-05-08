package faang.school.paymentservice.service.currency.implementations;

import faang.school.paymentservice.dto.StringExchangeRateResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.net.URI;
import java.util.Map;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PrimaryExchangeRateProviderTest {

    @InjectMocks
    private PrimaryExchangeRateProvider provider =
            new PrimaryExchangeRateProvider("https://api.example.com", "testKey", "latest");

    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;
    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec;
    @Mock
    private WebClient.ResponseSpec responseSpec;
    @Mock
    private WebClient webClient;

    @BeforeEach
    void setUp() throws Exception {
        provider = new PrimaryExchangeRateProvider("https://api.example.com", "testKey", "latest");

        Field field = AbstractExchangeRateProvider.class.getDeclaredField("webClient");
        field.setAccessible(true);
        field.set(provider, webClient);
    }

    @Test
    void testFetchResponseWhenReturnValidResponse() {
        StringExchangeRateResponse expectedResponse = StringExchangeRateResponse.builder()
                .timestamp(123456789)
                .base("EUR")
                .rates(Map.of("EUR", BigDecimal.ONE, "USD", new BigDecimal("0.9")))
                .build();

        URI uri = URI.create("https://api.example.com/testKey/latest");


        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(StringExchangeRateResponse.class)).thenReturn(Mono.just(expectedResponse));

        StringExchangeRateResponse actual = provider.fetchResponse();

        assertEquals(expectedResponse, actual);
    }

    @Test
    void testBuildUriWhenBuildCorrectUri() {
        URI uri = provider.buildUri().apply(UriComponentsBuilder.fromUriString("https://api.example.com"));

        assertEquals("https://api.example.com/testKey/latest", uri.toString());
    }

    @Test
    void testGetTimestampWhenReturnTimestamp() {
        StringExchangeRateResponse response = StringExchangeRateResponse.builder()
                .timestamp(1234)
                .build();

        assertEquals(1234, provider.getTimestamp(response));
    }

    @Test
    void testGetBaseWhenReturnBaseCurrency() {
        StringExchangeRateResponse response = StringExchangeRateResponse.builder()
                .base("EUR")
                .build();

        assertEquals("EUR", provider.getBase(response));
    }

    @Test
    void getRatesWhenReturnRatesMap() {
        Map<String, BigDecimal> rates = Map.of("EUR", BigDecimal.ONE);
        StringExchangeRateResponse response = StringExchangeRateResponse.builder()
                .rates(rates)
                .build();

        assertEquals(rates, provider.getRates(response));
    }
}