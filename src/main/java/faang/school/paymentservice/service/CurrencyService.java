package faang.school.paymentservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.HashMap;

@Service
@RequiredArgsConstructor
public class CurrencyService {
    private final WebClient client;

    public Mono<HashMap<String, Double>> getActualRates() {
        return client
                .get()
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<>() {});
    }
}
