package faang.school.paymentservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

@Configuration
@EnableRetry
public class RetryConfig {
    @Value("${external.currency.api.maxAttempts}")
    private int maxAttempts;

    @Value("${external.currency.api.backoff-delay-millis}")
    private long backoffDelay;

    @Bean(name = "currencyRetryTemplate")
    public RetryTemplate currencyRetryTemplate() {
        RetryTemplate template = new RetryTemplate();

        SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy(maxAttempts);
        FixedBackOffPolicy backoff = new FixedBackOffPolicy();
        backoff.setBackOffPeriod(backoffDelay);

        template.setRetryPolicy(retryPolicy);
        template.setBackOffPolicy(backoff);

        return template;
    }
}
