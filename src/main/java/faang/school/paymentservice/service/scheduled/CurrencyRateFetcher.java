package faang.school.paymentservice.service.scheduled;

import faang.school.paymentservice.dto.ExchangeRateResponse;
import faang.school.paymentservice.service.CurrencyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CurrencyRateFetcher {

    private static final String LATEST_RATES_KEY = "currency:latest";
    private final CurrencyService currencyService;
    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${currency.exchangerate.fetcher.currencies}")
    private String currencies;

    @Scheduled(cron = "${currency.exchangerate.fetcher.cron}")
    public void fetchAndStoreRates() {
        log.info("Начало обновления курсов валют по расписанию");
        try {
            ExchangeRateResponse response = currencyService.fetchLatestRates(currencies);
            if (response != null) {
                redisTemplate.opsForValue().set(LATEST_RATES_KEY, response);
                log.info("Курсы валют успешно обновлены и сохранены в Redis: {}", response);
            }
        } catch (Exception e) {
            log.error("Не удалось обновить курсы валют", e);
        }
    }

    public ExchangeRateResponse getLatestRates() {
        Object value = redisTemplate.opsForValue().get(LATEST_RATES_KEY);
        if (value instanceof ExchangeRateResponse) {
            return (ExchangeRateResponse) value;
        }
        return null;
    }
}
