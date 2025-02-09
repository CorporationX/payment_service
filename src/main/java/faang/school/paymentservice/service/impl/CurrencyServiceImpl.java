package faang.school.paymentservice.service.impl;

import faang.school.paymentservice.configuration.ExchangeRatesProperties;
import faang.school.paymentservice.service.CurrencyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientException;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class CurrencyServiceImpl implements CurrencyService {
    private final ExchangeRatesProperties properties;
    private final WebClient webClient;

    @Autowired
    public CurrencyServiceImpl(WebClient.Builder webClientBuilder, ExchangeRatesProperties properties) {
        this.properties = properties;
        this.webClient = webClientBuilder
                .baseUrl(properties.getBaseUrl())
                .build();
    }

    @Retryable(
            value = {WebClientException.class},
            maxAttempts = 5,
            backoff = @Backoff(delay = 1000, multiplier = 2)
    )
    public Mono<String> getCurrencyExchangeRates() {
        log.info("CurrencyServiceImpl#getCurrencyExchangeRates: trying to get exchange rates...");
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(properties.getEndpoints().getGetLatestRate())
                        .queryParam("access_key", properties.getAccessKey())
                        .build())
                .retrieve()
                .bodyToMono(String.class);
    }
}
