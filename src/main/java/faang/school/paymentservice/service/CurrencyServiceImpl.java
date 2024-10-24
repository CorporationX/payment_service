package faang.school.paymentservice.service;

import faang.school.paymentservice.config.ExchangeRatesProperties;
import faang.school.paymentservice.dto.Rate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class CurrencyServiceImpl implements CurrencyService {
    private final ExchangeRatesProperties exchangeRatesProperties;
    private final WebClient webClient;

    @Override
    @Retryable(maxAttempts = 5, backoff = @Backoff(delay = 3000))
    @Cacheable(value = "current_rate")
    public Rate updateCurrency() {
        Rate response = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/latest")
                        .queryParam("access_key", exchangeRatesProperties.getKey())
                        .build())
                .retrieve()
                .bodyToMono(Rate.class)
                .onErrorResume(Mono::error)
                .block();
        log.info("rate got: " + response);
        return response;
    }
}

