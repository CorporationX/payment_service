package faang.school.paymentservice.model;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "exchangerates.api")
public record LatestRatesEndpoint(String url, String access_key, String base, String symbols) {
}
