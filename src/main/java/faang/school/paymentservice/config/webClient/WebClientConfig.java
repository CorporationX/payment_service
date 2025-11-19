package faang.school.paymentservice.config.webClient;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }

    public WebClient getWebClient(String baseUrl, String header, String values) {
        return webClientBuilder()
                .baseUrl(baseUrl)
                .defaultHeader(header, values)
                .build();
    }
}