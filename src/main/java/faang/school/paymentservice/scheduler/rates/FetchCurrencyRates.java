package faang.school.paymentservice.scheduler.rates;

import faang.school.paymentservice.service.rates.CurrencyRatesService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Map;

@RequiredArgsConstructor
@Slf4j
@Component
public class FetchCurrencyRates {
    private final RedisTemplate<String, Double> redisTemplate;
    private final CurrencyRatesService currencyRatesService;

    @Scheduled(cron = "${scheduler.fetch.currency-rates.cron}")
    public void fetchCurrencyRatesScheduled() {
        Map<String, Double> rates = currencyRatesService.fetch();

        rates.forEach((key, value) -> redisTemplate.opsForValue().set(key, value));
        log.info("Actual currency rates fetched and updated in Redis");
    }
}
