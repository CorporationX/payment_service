package faang.school.paymentservice.repository;

import faang.school.paymentservice.dto.Currency;
import faang.school.paymentservice.dto.CurrencyRate;
import faang.school.paymentservice.exception.CurrencyRateException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Repository
public class CurrencyRateRepository {
    public static final String REDIS_KEY_CURRENCY_RATE = "currency_rate";
    public static final String TIMESTAMP = "timestamp";

    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${currency.rate.updateExchangeRatesMillis}")
    private long updateExchangeRatesMillis;

    public CurrencyRateRepository(@Qualifier("currencyRatesRedisTemplate") RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void saveCurrencyRate(CurrencyRate currencyRate) {
        try {
            Map<String, Object> hash = new HashMap<>();
            hash.put(TIMESTAMP, currencyRate.getTimestamp().toString());

            currencyRate.getRates().forEach((currency, rate) -> hash.put(currency.toString(), rate));
            redisTemplate.opsForHash()
                    .putAll(REDIS_KEY_CURRENCY_RATE, hash);
            redisTemplate.expire(REDIS_KEY_CURRENCY_RATE, updateExchangeRatesMillis, TimeUnit.MILLISECONDS);

        } catch (RuntimeException e) {
            log.error("Error while saving to Redis", e);
            throw new CurrencyRateException(e.getMessage());
        }
    }

    public Map<Currency, Double> getCurrencyRates(Currency first, Currency second) {
        try {
            List<Object> values = redisTemplate.opsForHash()
                    .multiGet(REDIS_KEY_CURRENCY_RATE, List.of(first.toString(), second.toString()));

            return Map.of(
                    first, (Double) values.get(0),
                    second, (Double) values.get(1));

        } catch (RuntimeException e) {
            log.error("Error while get from Redis for currencies {}/{}", first, second, e);
            throw new CurrencyRateException("Fail to get currency rates, try again later");
        }
    }

    public LocalDateTime getCreatedTime() {
        try {
            String timestamp = (String) redisTemplate.opsForHash()
                    .get(REDIS_KEY_CURRENCY_RATE, TIMESTAMP);
            return LocalDateTime.parse(timestamp);
        } catch (RuntimeException e) {
            log.error("Error while getting timestamp from Redis", e);
            throw new CurrencyRateException("Fail to get timestamp, try again later");
        }
    }
}
