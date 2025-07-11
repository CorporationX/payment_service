package faang.school.paymentservice.config.job;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@ConfigurationProperties(prefix = "jobs.payment-operation.clearing")
@Configuration
public class JobClearingPaymentConfig {
    private long scheduledAt;
    private String cron;
}
