package faang.school.paymentservice.config;

import faang.school.paymentservice.config.property.exchangerates.RetryProperty;
import faang.school.paymentservice.exception.ExternalApiException;
import faang.school.paymentservice.exception.RetryExhaustedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.util.retry.Retry;

import java.time.Duration;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class RetryConfig {
    private final RetryProperty retryProperty;

    @Bean
    public Retry exchangeRatesRetry() {
        Duration minBackoff = Duration.of(retryProperty.delay(), retryProperty.delayTimeUnit());
        return Retry.backoff(retryProperty.maxAttempts(), minBackoff)
                .jitter(retryProperty.jitter())
                .filter(throwable -> throwable instanceof ExternalApiException)
                .doBeforeRetry(
                        retrySignal -> log.info("Attempt #{}, cause: {}",
                                                (retrySignal.totalRetries() + 1),
                                                retrySignal.failure().getMessage()))
                .onRetryExhaustedThrow(
                        (spec, signal) -> new RetryExhaustedException("Retries exhausted: {}/{}",
                                                                      signal.failure(),
                                                                      retryProperty.maxAttempts(),
                                                                      signal.totalRetries()));
    }
}
