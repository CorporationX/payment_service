package faang.school.paymentservice.client;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class FeignClientConfiguration {
    @Bean
    public RequestInterceptor requestInterceptor() {
        return new RequestInterceptor() {
            @Override
            public void apply(RequestTemplate template) {
                log.info("Request URL: {}", template.url());
                log.info("Request Method: {}", template.method());
                log.info("Request Headers: {}", template.headers());
                log.info("Request Body: {}", template.body());
            }
        };
    }
}