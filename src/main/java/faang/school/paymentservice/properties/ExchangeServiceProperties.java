package faang.school.paymentservice.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "services.exchange-service")
public class ExchangeServiceProperties {

    private String token;
    private double commissionRate;
    private String cronExpression;
}