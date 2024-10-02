package faang.school.paymentservice.client;

import faang.school.paymentservice.dto.ExchangeRatesDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Component
public class ExchangeRates {
    private final WebClient webClient = WebClient.builder()
            .baseUrl("https://api.exchangeratesapi.io")
            .build();

    public Mono<ExchangeRatesDto> fetchData() {
        return webClient.get()
                .uri("/v1/latest?access_key=a9a4c62d6dbea49c6ea892287f57fc24")
                .retrieve()
                .bodyToMono(ExchangeRatesDto.class)
                .onErrorResume(e->{
                    System.out.println("Something went wrong, continue working");
                    return Mono.empty();
                });
    }
}
