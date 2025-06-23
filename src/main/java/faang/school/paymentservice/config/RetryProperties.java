package faang.school.paymentservice.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("currency-rates.retry")
public record RetryProperties(
        int maxAttempts,
        long delaySeconds
) {
}