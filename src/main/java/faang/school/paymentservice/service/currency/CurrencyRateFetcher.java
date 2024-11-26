package faang.school.paymentservice.service.currency;

import faang.school.paymentservice.dto.RatesDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
public class CurrencyRateFetcher {

    private final WebClient webClient;

    @Value("${services.exchange.endpoint}")
    String endpoint;

    @Value("${services.exchange.key}")
    String key;

    public RatesDto fetchData() {
        return webClient.get()
                .uri(endpoint + key)
                .retrieve()
                .bodyToMono(RatesDto.class)
                .block();
    }
}
