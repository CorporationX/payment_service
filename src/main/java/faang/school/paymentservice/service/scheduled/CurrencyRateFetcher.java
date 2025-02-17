package faang.school.paymentservice.service.scheduled;

import faang.school.paymentservice.dto.ExchangeRateResponse;
import faang.school.paymentservice.service.CurrencyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CurrencyRateFetcher {

    private static final String LATEST_RATES_KEY = "currency:latest";
    private final CurrencyService currencyService;
    private final RedisTemplate<String, ExchangeRateResponse> redisTemplate;

    @Scheduled(cron = "${currency.exchangerate.fetcher.cron}")
    public void fetchAndStoreRates() {
        log.info("Начало обновления курсов валют по расписанию");
        try {
            ExchangeRateResponse response = currencyService.fetchLatestRates("USD,AUD,CAD,PLN,MXN");
            if (response != null) {
                redisTemplate.opsForValue().set(LATEST_RATES_KEY, response);
                log.info("Курсы валют успешно обновлены и сохранены в Redis: {}", response);
            }
        } catch (Exception e) {
            log.error("Не удалось обновить курсы валют", e);
        }
    }

    public ExchangeRateResponse getLatestRates() {
        return redisTemplate.opsForValue().get(LATEST_RATES_KEY);
    }
}
