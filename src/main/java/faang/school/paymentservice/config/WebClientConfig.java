package faang.school.paymentservice.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class WebClientConfig {
    private final WebClientProperties properties;

    @Bean
    public WebClient currencyWebClient(WebClient.Builder webClientBuilder) {
        return webClientBuilder
                .baseUrl(properties.baseUrl())
                .clientConnector(new ReactorClientHttpConnector(
                        HttpClient.create()
                                .responseTimeout(Duration.ofSeconds(properties.timeoutSeconds()))
                ))
                .build();
    }
}