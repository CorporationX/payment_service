package faang.school.paymentservice.client.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "currency-converter-api.auth")
@Data
public class CurrencyConverterApIAuthProperties {
    private String token;
}
