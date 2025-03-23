package faang.school.paymentservice.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Slf4j
@Configuration
public class ExecutorServiceConfig {
    private static final int DMS_SCHEDULED_CORE_POOL_SIZE = 1;
    private static final int DMS_SCHEDULE_MAXIMUM_POOL_SIZE = 1;
    private static final long DMS_KEEP_ALIVE_TIME = 0L;

    @Bean(name = "scheduledSendAuthExecutorService")
    public ExecutorService scheduledSendAuthExecutorService() {
        BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<>(1);

        return new ThreadPoolExecutor(DMS_SCHEDULED_CORE_POOL_SIZE, DMS_SCHEDULE_MAXIMUM_POOL_SIZE, DMS_KEEP_ALIVE_TIME,
                TimeUnit.SECONDS, workQueue, new ThreadPoolExecutor.AbortPolicy());
    }

    @Bean(name = "scheduledSendCancelExecutorService")
    public ExecutorService scheduledSendCancelExecutorService() {
        BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<>(1);

        return new ThreadPoolExecutor(DMS_SCHEDULED_CORE_POOL_SIZE, DMS_SCHEDULE_MAXIMUM_POOL_SIZE, DMS_KEEP_ALIVE_TIME,
                TimeUnit.SECONDS, workQueue, new ThreadPoolExecutor.AbortPolicy());
    }

    @Bean(name = "scheduledSendClearingExecutorService")
    public ExecutorService scheduledSendClearingExecutorService() {
        BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<>(1);

        return new ThreadPoolExecutor(DMS_SCHEDULED_CORE_POOL_SIZE, DMS_SCHEDULE_MAXIMUM_POOL_SIZE, DMS_KEEP_ALIVE_TIME,
                TimeUnit.SECONDS, workQueue, new ThreadPoolExecutor.AbortPolicy());
    }
}
