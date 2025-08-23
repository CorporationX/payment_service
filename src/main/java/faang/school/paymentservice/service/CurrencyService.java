package faang.school.paymentservice.service;

import faang.school.paymentservice.dto.RatesApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class CurrencyService {
    private final WebClient client;

    @Retryable(retryFor = {Exception.class}, maxAttempts = 3,
            backoff = @Backoff(delay = 1000, multiplier = 2))
    public Mono<RatesApiResponse> getRatesApiResponse() {
        return client
                .get()
                .retrieve()
                .bodyToMono(RatesApiResponse.class);
    }
}
