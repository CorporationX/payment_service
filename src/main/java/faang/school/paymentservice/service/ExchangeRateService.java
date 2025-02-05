package faang.school.paymentservice.service;

import faang.school.paymentservice.config.CurrencyRateConfig;
import faang.school.paymentservice.entity.CurrencyRateDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.time.LocalDateTime;

@Slf4j
@Service
public class ExchangeRateService {
    private final CurrencyRateConfig config;
    private final WebClient webClient;
    @Value("${currency.rate.accessKey}")
    private String accessKey;

    public ExchangeRateService(@Qualifier("currencyRateWebClient") WebClient webClient, CurrencyRateConfig config) {
        this.webClient = webClient;
        this.config = config;
    }

    public Mono<CurrencyRateDto> getCurrencyRateFromApi() {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/latest")
                        .queryParam("access_key", accessKey)
                        .build())
                .retrieve()
                .bodyToMono(CurrencyRateDto.class)
                .retryWhen(Retry.fixedDelay(
                        config.getConnectionRetryAttempts(), Duration.ofSeconds(config.getConnectionRetrySeconds())))
                .doOnSuccess(currencyRate -> log.info("Successfully got currency rate from API {}", LocalDateTime.now()))
                .doOnError(error -> log.error("Cannot get exchange rates ", error));
    }
}
