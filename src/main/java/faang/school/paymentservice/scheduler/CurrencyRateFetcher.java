package faang.school.paymentservice.scheduler;

import faang.school.paymentservice.dto.Rates;
import faang.school.paymentservice.redis.RedisCacheService;
import faang.school.paymentservice.service.CurrencyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CurrencyRateFetcher {
    private final RedisCacheService cacheService;
    private final CurrencyService service;

    @Scheduled(fixedRate = 10000)
    private void ratesCron() {
        service.getRatesApiResponse()
                .subscribe(responseRates -> {
                    log.info("Курсы валют: {}", getActualRates());
                    cacheService.cacheRates("currency_rates", responseRates.rates);
                });
    }

    public Rates getActualRates() {
        return cacheService.getCachedRates("currency_rates");
    }
}
