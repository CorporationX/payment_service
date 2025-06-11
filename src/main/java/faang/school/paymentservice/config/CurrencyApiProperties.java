package faang.school.paymentservice.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "currency.api")
public class CurrencyApiProperties {
    private String url;
    private String accessKey;
    private Retry retry;

    @Data
    public static class Retry {
        private int maxAttempts;
        private long delay;
        private double multiplier;
    }
}
