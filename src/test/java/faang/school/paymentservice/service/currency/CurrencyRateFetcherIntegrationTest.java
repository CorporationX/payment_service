package faang.school.paymentservice.service.currency;

import faang.school.paymentservice.client.api.CurrencyApiClient;
import faang.school.paymentservice.dto.CurrencyRatesDto;
import faang.school.paymentservice.scheduler.CurrencyRateFetcher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class CurrencyRateFetcherIntegrationTest {

    static GenericContainer<?> redisContainer = new GenericContainer<>("redis:6.0.9")
            .withExposedPorts(6379)
            .waitingFor(Wait.forListeningPort());

    @MockBean
    private CurrencyApiClient currencyApiClient;

    @Autowired
    private CurrencyService currencyService;

    @Autowired
    private CurrencyRateFetcher currencyRateFetcher;

    @BeforeEach
    public void setUp() {
        redisContainer.start();
        assertThat(redisContainer.isRunning()).isTrue();
    }

    @Test
    public void testScheduledCurrencyRateFetch() {
        Map<String, Object> mockResponse = new HashMap<>();
        Map<String, Object> conversionRates = new HashMap<>();
        conversionRates.put("USD", 1.0);
        conversionRates.put("EUR", 0.85);
        mockResponse.put("conversion_rates", conversionRates);

        when(currencyApiClient.getAllCurrencyRates()).thenReturn(Mono.just(mockResponse));

        currencyRateFetcher.fetchCurrencyRate();

        CurrencyRatesDto ratesFromRedis = currencyService.getAllCurrencyRatesFromRedis();
        assertThat(ratesFromRedis.getConversionRates().get("USD")).isEqualTo(new BigDecimal("1.0"));
        assertThat(ratesFromRedis.getConversionRates().get("EUR")).isEqualTo(new BigDecimal("0.85"));
    }
}