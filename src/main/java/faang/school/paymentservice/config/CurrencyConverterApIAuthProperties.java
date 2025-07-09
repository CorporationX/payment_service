package faang.school.paymentservice.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "currency-converter-api.auth")
@Data
public class CurrencyConverterApIAuthProperties {
    private String token;
}
