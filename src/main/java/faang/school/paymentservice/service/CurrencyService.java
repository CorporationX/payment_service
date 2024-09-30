package faang.school.paymentservice.service;

import faang.school.paymentservice.dto.RateResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class CurrencyService {
    @Value("${exchange_rates.key}")
    private String key;
    @Value("${exchange_rates.base}")
    private String base;

    private final WebClient webClient;


    @Retryable(maxAttempts = 5, backoff = @Backoff(delay = 3000))
    public void updateCurrency() {
        Mono<RateResponse> response = webClient.get()
                .uri(String.join("", "/latest?access_key =" + key + "& base=" + base))
                .retrieve()
                .bodyToMono(RateResponse.class);
        log.info("rate got: " + response);

    }
}
