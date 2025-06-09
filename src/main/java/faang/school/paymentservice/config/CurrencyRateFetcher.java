package faang.school.paymentservice.config;

import faang.school.paymentservice.dto.ExchangeRateResponseDto;
import faang.school.paymentservice.service.CurrencyService;
import faang.school.paymentservice.service.RedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@Slf4j
public class CurrencyRateFetcher {
    private final CurrencyService currencyService;
    private final RedisService redisService;

    @Scheduled(cron = "${cron.expression}")
    @Retryable(retryFor = {RuntimeException.class}, maxAttempts = 5, backoff = @Backoff(delay = 1000, multiplier = 2))
    public void saveCurrency(){
        Mono<ExchangeRateResponseDto> jsonMono = currencyService.saveExchangeRate();

        jsonMono.subscribe(response->redisService.setValue("USD", response.getRates().get("USD")));
//        jsonMono.subscribe(response->redisService.setValue("AUD", response.getRates().get("AUD")));
        jsonMono.subscribe(response->redisService.setValue("EUR", response.getRates().get("EUR")));
        log.info("The USD and EUR exchange rates have been updated");
    }
}
