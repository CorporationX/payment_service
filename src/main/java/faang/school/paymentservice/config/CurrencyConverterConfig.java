package faang.school.paymentservice.config;

import feign.RequestInterceptor;
import feign.Retryer;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
@RequiredArgsConstructor
public class CurrencyConverterConfig {

    private final CurrencyConverterApIAuthProperties authProperties;
    private final CurrencyConverterConfigurationProperties configurationProperties;

    @Bean
    public RequestInterceptor authRequestInterceptor() {
        return requestTemplate -> {
            String authToken = authProperties.getToken();
            requestTemplate.header("Authorization", "Token " + authToken);
        };
    }

    @Bean
    public Retryer feignRetryer() {
        return new Retryer.Default(
                configurationProperties.getRetryDelay(),
                TimeUnit.SECONDS.toMillis(configurationProperties.getMaxPeriod()),
                configurationProperties.getMaxAttempts()
        );
    }
}
