package faang.school.paymentservice.config.property.exchangerates;

import lombok.NonNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.boot.context.properties.bind.DefaultValue;

@ConfigurationProperties(prefix = "exchangerates.api")
public record ExchangeRatesProperty(
        @NonNull String accessKey,
        @NonNull String baseUrl,
        @DefaultValue("EUR")
        String baseCurrency,
        @DefaultValue("exchangerates")
        String cacheName,
        @DefaultValue("5000") int connectTimeoutMs,
        @DefaultValue("5000") int responseTimeoutMs,
        @NestedConfigurationProperty
        UriProperty uri,
        @NestedConfigurationProperty
        RetryProperty retry
) {}
