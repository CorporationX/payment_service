package faang.school.paymentservice.client;

import faang.school.paymentservice.config.context.UserContext;
import feign.RequestInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class RatesClientConfig {
    private final UserContext userContext;

    @Bean
    public RequestInterceptor authInterceptor() {
        return template -> template.header("Authorization", "Token " + userContext.getOpenAppId());
    }
}