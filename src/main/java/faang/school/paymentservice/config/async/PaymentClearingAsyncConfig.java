package faang.school.paymentservice.config.async;

import faang.school.paymentservice.config.properties.PaymentClearingAsyncProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@RequiredArgsConstructor
public class PaymentClearingAsyncConfig {

    private final PaymentClearingAsyncProperties paymentClearingProperties;

    @Bean(name = "paymentClearing")
    public ThreadPoolTaskExecutor createTariffRateCalculatorExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(paymentClearingProperties.poolSize());
        executor.setThreadNamePrefix(paymentClearingProperties.threadNamePrefix());
        executor.setWaitForTasksToCompleteOnShutdown(paymentClearingProperties.isWaitShutdown());
        executor.setAwaitTerminationSeconds(paymentClearingProperties.shutdownTimeoutSeconds());
        executor.initialize();
        return executor;
    }
}
