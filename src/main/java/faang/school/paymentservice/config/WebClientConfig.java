package faang.school.paymentservice.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;

@Configuration
@Slf4j
public class WebClientConfig {
    @Value("${currency-rates.api.base-url}")
    private String apiBaseUrl;
    @Value("${currency-rates.api.timeout-seconds}")
    private int timeoutSeconds;

    @Bean
    public WebClient currencyWebClient(WebClient.Builder webClientBuilder) {
        log.info("Initializing currency WebClient with base URL: {} and timeout: {}s",
                apiBaseUrl, timeoutSeconds);

        return webClientBuilder
                .baseUrl(apiBaseUrl)
                .clientConnector(new ReactorClientHttpConnector(
                        HttpClient.create()
                                .responseTimeout(Duration.ofSeconds(timeoutSeconds))
                ))
                .build();
    }
}