package faang.school.paymentservice.service.currency.rates;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import java.io.IOException;
import java.time.Duration;
import java.util.HashMap;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


@ExtendWith(MockitoExtension.class)
public class ExchangeRatesServiceTest {

    @Mock
    private WebClient webClient;

    @Mock
    private RedisTemplate<String, Double> redisTemplate;

    private ExchangeRatesService exchangeRatesService;

    public static MockWebServer mockWebServer;

    @BeforeEach
    void init() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
        String baseUrl = String.format("http://localhost:%s", mockWebServer.getPort());
        WebClient.Builder webClientbuilder = WebClient.builder();
        webClient = webClientbuilder
                .baseUrl(baseUrl)
                .build();
        exchangeRatesService = new ExchangeRatesService(webClient, redisTemplate);
    }

    @AfterEach
    void tearDown() throws Exception {
        mockWebServer.shutdown();
    }

    @Test
    void testSuccessfulAttemptToGetExchangeRates() throws Exception {
        String mockCurrency = getMockedResponse().toString();
        ObjectMapper objectMapper = new ObjectMapper();

        mockWebServer.enqueue(new MockResponse()
                .setBody(objectMapper.writeValueAsString(mockCurrency))
                .addHeader("Content-Type", "application/json"));

        Mono<String> currencyMono = exchangeRatesService.getExchangeRates();

        StepVerifier.create(currencyMono)
                .assertNext(currency -> {
                    assertNotNull(currency);
                    assertEquals(mockCurrency, currency.replace("\"", ""));
                })
                .thenCancel()
                .verify(Duration.ofSeconds(5));
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
}
