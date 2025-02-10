package faang.school.paymentservice.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Setter
@Getter
@ConfigurationProperties(prefix = "currency.rate")
@Configuration
public class CurrencyRateConfig {
    private String apiUrl;
    private int connectionTimeoutSeconds;
    private long readTimeoutSeconds;
    private long connectionRetrySeconds;
    private long connectionRetryAttempts;
    private double conversionRateFactor;
}
