package faang.school.paymentservice.service;

import faang.school.paymentservice.config.CurrencyApiProperties;
import faang.school.paymentservice.dto.ExchangeRatesResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Service
@Slf4j
@RequiredArgsConstructor
public class ExchangeRatesClient {

    private final WebClient webClient;
    private final CurrencyApiProperties properties;

    public ExchangeRatesResponse getResponse() {
        return webClient.get()
                .uri(properties.getUrl() + "?access_key=" + properties.getAccessKey())
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> {
                    HttpStatus status = HttpStatus.valueOf(clientResponse.statusCode().value());
                    log.error("Client error: {} {}", status.value(), status.getReasonPhrase());
                    return Mono.error(new RuntimeException("Client Error: " + status.value() + " " + status.getReasonPhrase()));
                })
                .onStatus(HttpStatusCode::is5xxServerError, clientResponse -> {
                    HttpStatus status = HttpStatus.valueOf(clientResponse.statusCode().value());
                    log.error("Server error: {} {}", status.value(), status.getReasonPhrase());
                    return Mono.error(new RuntimeException("Server Error: " + status.value() + " " + status.getReasonPhrase()));
                })
                .bodyToMono(ExchangeRatesResponse.class)
                .timeout(Duration.ofSeconds(30))
                .block();

    }
}
