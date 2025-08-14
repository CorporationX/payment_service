package faang.school.paymentservice.config;

import faang.school.paymentservice.config.property.exchangerates.ExchangeRatesProperty;
import io.netty.channel.ChannelOption;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class WebClientConfig {
    private final ExchangeRatesProperty exchangeRatesProperty;

    @Bean
    public WebClient exchangeRatesWebClient() {
        HttpClient client = HttpClient
                .create()
                .baseUrl(exchangeRatesProperty.baseUrl())
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, exchangeRatesProperty.connectTimeoutMs())
                .responseTimeout(Duration.ofMillis(exchangeRatesProperty.responseTimeoutMs()));

        return WebClient
                .builder()
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .clientConnector(new ReactorClientHttpConnector(client))
                .build();
    }
}
