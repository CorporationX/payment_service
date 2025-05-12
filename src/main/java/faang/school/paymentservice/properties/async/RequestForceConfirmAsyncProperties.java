package faang.school.paymentservice.properties.async;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "thread-pool-setting.request-force-confirming")
public record RequestForceConfirmAsyncProperties(
        int poolSize,
        int shutdownTimeoutSeconds,
        String threadNamePrefix,
        boolean isWaitShutdown
) implements AsyncProperties {}
