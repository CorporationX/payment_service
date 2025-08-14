package faang.school.paymentservice.config.property.redis;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import java.time.temporal.ChronoUnit;

@ConfigurationProperties(prefix = "redis")
public record RedisProperty(
        @DefaultValue("1") int ttl,
        @DefaultValue("DAYS") ChronoUnit ttlUnit,
        @DefaultValue("60") int defaultTtl,
        @DefaultValue("MINUTES") ChronoUnit defaultTtlUnit
) {}
