package faang.school.paymentservice.config.property.exchangerates;

import lombok.NonNull;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "exchangerates.api.uri")
public record UriProperty(
        @NonNull String latest
) {}
