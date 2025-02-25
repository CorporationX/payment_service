package faang.school.paymentservice.config;

import faang.school.paymentservice.dto.Currency;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "currency.exchange")
public record ExchangeCurrencyProperties(
        String url,
        String appId,
        Currency base,
        double commission
) {
}
