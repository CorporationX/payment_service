package faang.school.paymentservice.service;

import faang.school.paymentservice.dto.RateResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class CurrencyService {
    @Value("${exchange_rates.key}")
    private String key;
    @Value("${exchange_rates.base}")
    private String base;

    private final WebClient webClient;


    public void updateCurrency() {
        Mono<RateResponse> response = webClient.get()
                .uri(String.join("", "/latest?access_key =" + key + "& base=" + base))
                .retrieve()
                .bodyToMono(RateResponse.class);
    }
}
