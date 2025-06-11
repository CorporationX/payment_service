package faang.school.paymentservice.client;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "open-exchange-rates")
public record OpenexchangeConfig(
    String appId,
    String conversionCommission 
) {

}
