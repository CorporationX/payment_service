package faang.school.paymentservice.config.currencyRate;


import faang.school.paymentservice.config.webClient.WebClientConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class CurrencyRateFetcherConfig {
    private final WebClientConfig webClientConfig;
    @Value("${current-rate.base-url-rate}")
    private String baseUrl;
    @Value("${current-rate.header}")
    private String header;
    @Value("${current-rate.values}")
    private String values;

    @Retryable(
            retryFor = {WebClientRequestException.class, WebClientResponseException.class},
            backoff = @Backoff(delayExpression = "${current-rate.retry-delay}",
                    multiplierExpression = "${current-rate.count-invoke}"))
    public String getCurrentRate() {
        log.debug("Starting update current rate");
        WebClient webClient = webClientConfig.getWebClient(baseUrl, header, values);
        return webClient.get()
                .uri("_json.js")
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }
}