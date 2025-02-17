package faang.school.paymentservice.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "currency.exchangerate")
public class ExchangeRateConfig {

    private String url;
    private String apiKey;
}
