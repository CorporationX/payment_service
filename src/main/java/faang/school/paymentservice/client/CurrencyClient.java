package faang.school.paymentservice.client;

import faang.school.paymentservice.config.currencyrate.CurrencyRateConfig;
import faang.school.paymentservice.config.currencyrate.CurrencyRateParams;
import faang.school.paymentservice.dto.CurrencyRateDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.util.retry.Retry;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class CurrencyClient {
    private final CurrencyRateParams param;
    private final WebClient webClient;

    public CurrencyRateDto getCurrencyRates(String baseCurrency, String symbols) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("access_key", param.getAccessKey())
                        .queryParam("base", baseCurrency)
                        .queryParam("symbols", symbols)
                        .build()
                )
                .retrieve()
                .bodyToMono(CurrencyRateDto.class)
                .retryWhen(Retry.fixedDelay(3, Duration.ofSeconds(1)))
                .block();
    }
}
