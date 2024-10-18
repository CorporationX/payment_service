package faang.school.paymentservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CurrencyConfig {
    @Value("${payment.exchange-rates.api.response-json-field-name}")
    private String conversionRatesFieldName;

    @Value("${payment.exchange-rates.api.url}")
    private String currencyAPIUrl;

    @Value("${payment.exchange-rates.api.key}")
    private String currencyAPIKey;

    @Value("${payment.exchange-rates.api.rate-usd}")
    private String conversionRateUsd;


    @Bean
    public String currencyAPIUrl() {
        return generateUrl(currencyAPIUrl, currencyAPIKey, conversionRateUsd);
    }

    @Bean
    public String conversionRatesFieldName() {
        return conversionRatesFieldName;
    }

    private String generateUrl(String apiUrl, String apiKey, String conversionRateUsd) {
        if (apiKey == null || conversionRateUsd == null || apiUrl == null) {
            throw new IllegalArgumentException("Missing required properties for currency service: apiKey, conversionRateUsd, apiUrl");
        }
        return apiUrl.replace("{key}", apiKey).replace("{conversion-rate}", conversionRateUsd);
    }

}
