package faang.school.paymentservice.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "exchange-rates")
public class ExchangeRatesProperties {

    private String baseUrl;
    private String accessKey;
    private EndpointProperties endpoints;
    private SchedulerProperties scheduler;

    @Data
    public static class EndpointProperties {
        private String getLatestRate;
    }

    @Data
    public static class SchedulerProperties {
        private String cron;
        private long fixedRate;
        private long initialDelay;
    }
}
