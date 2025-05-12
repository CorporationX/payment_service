package faang.school.paymentservice.properties.async;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "thread-pool-setting.request-sender")
public record RequestSenderAsyncProperties(
        int poolSize,
        int shutdownTimeoutSeconds,
        String threadNamePrefix,
        boolean isWaitShutdown
) implements AsyncProperties {}
