package faang.school.paymentservice.service.rates;

import faang.school.paymentservice.dto.client.ExchangeRatesResponse;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientException;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CurrencyService {
    @Value("${currency.exchange.apiKey}")
    private String API_KEY;
    @Value("${currency.exchange.actualCurrency}")
    private String ACTUAL_CURRENCY;
    @Value("${currency.exchange.baseCurrency}")
    private String BASE_CURRENCY;
    private final WebClient webClient;


    public Map<String, Double> getActualRates() {
        Map<String, Double> rates = new HashMap<>();
        try {
            rates.putAll((fetchExchangeRates().getRates()));
        } catch (WebClientException e) {
            log.error("Failed to fetch rates from external API", e);
        }
        return rates;
    }

    @Retryable(retryFor = FeignException.class, maxAttempts = 10, backoff = @Backoff(delay = 1000, multiplier = 3))
    public ExchangeRatesResponse fetchExchangeRates() {
        return webClient
                .get()
                .uri(uriBuilder -> uriBuilder.path("/latest")
                        .queryParam("access_key", API_KEY)
                        .queryParam("symbols", ACTUAL_CURRENCY)
                        .queryParam("base", BASE_CURRENCY)
                        .build())
                .retrieve()
                .bodyToMono(ExchangeRatesResponse.class)
                .block();
    }
}
