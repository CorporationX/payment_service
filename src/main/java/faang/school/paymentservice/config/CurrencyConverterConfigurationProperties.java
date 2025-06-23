package faang.school.paymentservice.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "currency-converter-api")
public class CurrencyConverterConfigurationProperties {
    private int retryDelay;
    private int maxAttempts;
    private int maxPeriod;
    private String url;
    private double commissionPercentage;
    private String refreshCron;
}
