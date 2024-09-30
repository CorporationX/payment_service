package faang.school.paymentservice.config.currency;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "currency.exchange")
@Data
public class CurrencyExchangeConfig {
    private String url;
    private String apiKey;
    private Double commission;
}
