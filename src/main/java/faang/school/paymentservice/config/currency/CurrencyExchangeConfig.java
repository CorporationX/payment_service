package faang.school.paymentservice.config.currency;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Configuration
@Data
public class CurrencyExchangeConfig {
    @Value("${currency.fetch.url}")
    private String url;
    @Value("${currency.fetch.appId}")
    private String appId;
    @Value("${currency.fetch.commission}")
    private Double commission;
}