package faang.school.paymentservice.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "currency.exchange")
public record CurrencyExchangeConfig(String url, String appId, Double commission) {
}