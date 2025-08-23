package faang.school.paymentservice.scheduler;

import faang.school.paymentservice.dto.Rates;
import faang.school.paymentservice.redis.RedisCacheService;
import faang.school.paymentservice.service.CurrencyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Класс, отвечающий за вызов метода сервиса {@link CurrencyService} с целью получения
 * актуального курса валют и последующего кэширования в Redis.
 */

@Slf4j
@Component
@RequiredArgsConstructor
public class CurrencyRateFetcher {
    private final RedisCacheService cacheService;
    private final CurrencyService service;

    /**
     * Шедулер для получения и кэширования курса валют. Запуск производится раз в сутки.
     */
    @Scheduled(cron = "${currency.rates.cron}")
    private void ratesCron() {
        service.getRatesApiResponse()
                .subscribe(responseRates -> {
                    log.info("Курсы валют: {}", getActualRates());
                    cacheService.cacheRates("currency_rates", responseRates.rates);
                });
    }

    /**
     * @return актуальный курс валют в виде DTO {@link Rates}
     */
    public Rates getActualRates() {
        return cacheService.getCachedRates("currency_rates");
    }
}
