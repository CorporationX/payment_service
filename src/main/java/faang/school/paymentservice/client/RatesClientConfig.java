package faang.school.paymentservice.client;

import feign.RequestInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class RatesClientConfig {
    @Value("${services.open_exchange.app_id}")
    private String openAppId;

    @Bean
    public RequestInterceptor authInterceptor() {
        return template -> template.header("Authorization", "Token " + openAppId);
    }
}