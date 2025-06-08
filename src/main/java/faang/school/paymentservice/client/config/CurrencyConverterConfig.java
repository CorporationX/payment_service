package faang.school.paymentservice.client.config;

import feign.RequestInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class CurrencyConverterConfig {

    private final CurrencyConverterApIAuthProperties properties;

    @Bean
    public RequestInterceptor authRequestInterceptor() {
        return requestTemplate -> {
            String authToken = properties.getToken();
            requestTemplate.header("Authorization", "Token " + authToken);
        };
    }
}
