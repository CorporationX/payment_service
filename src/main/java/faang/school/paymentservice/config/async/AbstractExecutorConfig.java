package faang.school.paymentservice.config.async;

import faang.school.paymentservice.properties.async.AsyncProperties;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

public abstract class AbstractExecutorConfig extends ThreadPoolTaskExecutor {

    protected void createThreadPool(AsyncProperties properties) {
        setCorePoolSize(properties.poolSize());
        setThreadNamePrefix(properties.threadNamePrefix());
        setWaitForTasksToCompleteOnShutdown(properties.isWaitShutdown());
        setAwaitTerminationSeconds(properties.shutdownTimeoutSeconds());
        initialize();
    }
}
