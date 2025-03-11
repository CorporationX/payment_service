package faang.school.paymentservice.config;

import faang.school.paymentservice.dto.Currency;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.math.BigDecimal;

@ConfigurationProperties(prefix = "currency.exchange")
public record ExchangeCurrencyProperties(
        String url,
        String appId,
        Currency base,
        BigDecimal commissionPercent
) {
}
