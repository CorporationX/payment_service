package faang.school.paymentservice.service.currency.rates;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.paymentservice.dto.Currency;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.io.IOException;
import java.time.Duration;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class ExchangeRatesServiceTest {

    @Mock
    private WebClient webClient;

    @Mock
    private RedisTemplate<String, Double> redisTemplate;

    private String mockCurrency;
    private ObjectMapper objectMapper;
    private ExchangeRatesService exchangeRatesService;
    private static MockWebServer mockWebServer;

    @BeforeEach
    void init() throws IOException {
        mockCurrency = getMockedResponse().toString();
        objectMapper = new ObjectMapper();
        mockWebServer = new MockWebServer();
        mockWebServer.start();

        String baseUrl = String.format("http://localhost:%s", mockWebServer.getPort());
        WebClient.Builder webClientbuilder = WebClient.builder();

        webClient = webClientbuilder
                .baseUrl(baseUrl)
                .build();

        exchangeRatesService = new ExchangeRatesService(webClient, redisTemplate);
        ReflectionTestUtils.setField(exchangeRatesService, "baseCurrency", Currency.EUR);
    }

    @AfterEach
    void tearDown() throws Exception {
        mockWebServer.shutdown();
    }

    @Test
    void testSuccessfulAttemptToGetExchangeRates() throws Exception {
        mockWebServer.enqueue(new MockResponse()
                .setBody(objectMapper.writeValueAsString(mockCurrency))
                .addHeader("Content-Type", "application/json"));

        String result = exchangeRatesService.getExchangeRates();

        assertEquals(mockCurrency, result.replace("\"", ""));
    }

    @Test
    public void testGetExchangeRatesWithWebClientResponseException() {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(404)
                .setBody("Not Found"));

        StepVerifier.create(Mono.fromCallable(() -> exchangeRatesService.getExchangeRates()))
                .expectError(WebClientResponseException.class)
                .verify();
    }

    @Test
    public void testGetExchangeRatesWithResourceAccessException() throws IOException {
        mockWebServer.shutdown();

        StepVerifier.create(Mono.fromCallable(() -> exchangeRatesService.getExchangeRates()))
                .expectError(WebClientRequestException.class)
                .verify();
    }

    private static HashMap<String, String> getMockedResponse() {
        HashMap<String, String> map = new HashMap<>();
        map.put("CAD", "1.483067");
        map.put("PLN", "4.306118");
        map.put("JPY", "159.731324");
        map.put("MXN", "21.814927");
        map.put("AUD", "1.625341");
        map.put("USD", "1.055342");
        map.put("EUR", "1.0");
        return map;
    }

    private String retryingGetExchangeRates() {
        return exchangeRatesService.getExchangeRates();
    }
}
