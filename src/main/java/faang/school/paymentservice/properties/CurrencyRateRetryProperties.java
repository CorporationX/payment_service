package faang.school.paymentservice.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "retry")
public record CurrencyRateRetryProperties(int maxAttempts, Long delay) {
}