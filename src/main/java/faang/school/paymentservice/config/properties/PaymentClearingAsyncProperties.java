package faang.school.paymentservice.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "thread-pool-setting.payment-clearing")
public record PaymentClearingAsyncProperties(
        int poolSize,
        int shutdownTimeoutSeconds,
        String threadNamePrefix,
        boolean isWaitShutdown
) {}
