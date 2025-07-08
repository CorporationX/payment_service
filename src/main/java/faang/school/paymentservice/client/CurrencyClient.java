package faang.school.paymentservice.client;

import faang.school.paymentservice.dto.ExchangeRateResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import static faang.school.paymentservice.utils.MessageConstants.FAILED_EXCHANGE_RATE_RESPONSE;

@Slf4j
@RequiredArgsConstructor
@Component
public class CurrencyClient {

    private final WebClient webClient;

    @Value("${currency.app-id}")
    private String appId;

    @Retryable(value = {RuntimeException.class}, maxAttempts = 5,
            backoff = @Backoff(value = 2000, multiplier = 2))
    public ExchangeRateResponseDto getLiveExchangeRate(String source, String currencies) {
        ExchangeRateResponseDto responseDto = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/live")
                        .queryParam("access_key", appId)
                        .queryParam("source", source)
                        .queryParam("currencies", currencies)
                        .build())
                .retrieve()
                .bodyToMono(ExchangeRateResponseDto.class)
                .block();
        log.info("getLiveExchangeRate source={}, currencies={}, response={}", source, currencies, responseDto);
        if(responseDto == null || !responseDto.isSuccess()) {
            log.error(FAILED_EXCHANGE_RATE_RESPONSE);
            throw new RuntimeException(FAILED_EXCHANGE_RATE_RESPONSE);
        }
        return responseDto;
    }
}
