package faang.school.paymentservice.config;

import io.netty.channel.ChannelOption;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;

@Setter
@Getter
@RequiredArgsConstructor
@Configuration
public class WebClientConfig {
    private final CurrencyRateConfig config;

    @Bean(name = "currencyRateWebClient")
    public WebClient currencyRateWebClient() {
        HttpClient httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, config.getConnectionTimeoutSeconds() * 1000)
                .responseTimeout(Duration.ofSeconds(config.getReadTimeoutSeconds()));

        ClientHttpConnector connector = new ReactorClientHttpConnector(httpClient);

        return WebClient.builder()
                .baseUrl(config.getApiUrl())
                .clientConnector(connector)
                .build();
    }
}
