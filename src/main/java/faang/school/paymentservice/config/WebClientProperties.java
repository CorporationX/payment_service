package faang.school.paymentservice.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("currency-rates.api")
public record WebClientProperties(
        String baseUrl,
        int timeoutSeconds
) {
}