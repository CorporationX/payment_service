package faang.school.paymentservice.config;

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
                log.info(String.format("Request URL: {%s}, Request Method: {%s},Request Headers: {%s}, Request Body: {%s}",
                        template.url(), template.method(), template.headers(), template.body()));
            }
        };
    }
}