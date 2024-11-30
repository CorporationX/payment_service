package faang.school.paymentservice.service;

import faang.school.paymentservice.dto.ExchangeRates;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CachePut;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Service
@RequiredArgsConstructor
public class CurrencyService {

    private final WebClient.Builder webClientBuilder;

    @Value("${thirdparty.exchangerates.requesturl}")
    private String requestUrl;

    @Value("${thirdparty.exchangerates.apikey}")
    private String apiKey;

    @CachePut(value = "exchangerates", key = "'exchangerates'")
    @Retryable(maxAttempts = 5, backoff = @Backoff(multiplier = 2))
    public ExchangeRates getCurrentExchangeRates() {
        log.info("Trying to fetch current exchange rates");
        return webClientBuilder.build()
                .get()
                .uri(requestUrl + apiKey)
                .retrieve()
                .bodyToMono(ExchangeRates.class)
                .block();
    }
}
