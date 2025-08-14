package faang.school.paymentservice.config.property.exchangerates;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

import java.time.temporal.ChronoUnit;

@ConfigurationProperties(prefix = "exchangerates.api.retry")
public record RetryProperty(
        @DefaultValue("3") long maxAttempts,
        @DefaultValue("2") long delay,
        @DefaultValue("SECONDS") ChronoUnit delayTimeUnit,
        @DefaultValue("0.0") double jitter
) {}
