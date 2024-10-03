package faang.school.paymentservice.client;

import faang.school.paymentservice.config.WebClientConfig;
import faang.school.paymentservice.dto.ExchangeRatesDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;

@Slf4j
@RequiredArgsConstructor
@Service
public class ExchangeRates {

    private final WebClientConfig webClientConfig;

    @Value("${services.exchangerates.appId}")
    private String appId;

    public Mono<ExchangeRatesDto> fetchData() {
        return webClientConfig.createWebClient()
                .get()
                .uri("/latestT?access_key=" + appId)
                .retrieve()
//                .onStatus(
//                        statusCode -> statusCode.is4xxClientError(),
//                        response -> Mono.error(new RuntimeException("Authorization error: " + response.statusCode()))
//                )
                .bodyToMono(ExchangeRatesDto.class)
                .onErrorResume(e -> {
                    log.error(e.getMessage(), e);
                    log.info("Something went wrong, continue working");
                    return Mono.error(e);
                })
                .retryWhen(Retry.backoff(2, Duration.ofSeconds(1)))
                .onErrorReturn(null); // TODO correct
    }
}
