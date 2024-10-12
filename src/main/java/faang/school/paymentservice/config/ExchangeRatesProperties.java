package faang.school.paymentservice.config;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@Data
@ConfigurationPropertiesScan
@ConfigurationProperties(prefix = "exchangerates")
public class ExchangeRatesProperties {
    @NotBlank
    private String baseUrl;
    @NotBlank
    private String key;
    @NotBlank
    @NotNull
    private String base;
}
