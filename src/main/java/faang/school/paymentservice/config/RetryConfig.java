package faang.school.paymentservice.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.util.retry.Retry;

import java.time.Duration;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class RetryConfig {
    private final RetryProperties properties;

    @Bean
    public Retry createRetrySpec() {
        return Retry.fixedDelay(properties.maxAttempts(), Duration.ofSeconds(properties.delaySeconds()))
                .filter(ex -> {
                    if (ex instanceof WebClientResponseException webEx) {
                        return webEx.getStatusCode().is4xxClientError() ||
                                webEx.getStatusCode().is5xxServerError();
                    }
                    return false;
                })
                .onRetryExhaustedThrow((spec, signal) -> {
                    log.error("Retry exhausted after {} attempts", signal.totalRetries());
                    return signal.failure();
                })
                .doBeforeRetry(this::logRetryAttempt);
    }


    private void logRetryAttempt(Retry.RetrySignal signal) {
        log.warn("Retry attempt #{} due to error: {}",
                signal.totalRetries() + 1,
                signal.failure().getMessage());
    }
}