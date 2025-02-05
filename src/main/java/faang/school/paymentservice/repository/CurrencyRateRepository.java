package faang.school.paymentservice.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.paymentservice.dto.CurrencyRate;
import faang.school.paymentservice.exception.CurrencyRateException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@Slf4j
@Repository
public class CurrencyRateRepository {
    public static final String REDIS_KEY_CURRENCY_RATE = "currency_rate";

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    @Value("${currency.rate.updateExchangeRatesMillis}")
    private long updateExchangeRatesMillis;

    public CurrencyRateRepository(
            @Qualifier("currencyRatesRedisTemplate") StringRedisTemplate redisTemplate,
            ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    public void saveCurrencyRate(CurrencyRate currencyRateDto) {
        try {
            String currencyRateJson = objectMapper.writeValueAsString(currencyRateDto);
            redisTemplate.opsForValue()
                    .set(REDIS_KEY_CURRENCY_RATE, currencyRateJson, updateExchangeRatesMillis, TimeUnit.MILLISECONDS);

        } catch (RuntimeException | JsonProcessingException e) {
            log.error("Error while saving to Redis", e);
            throw new CurrencyRateException(e.getMessage());
        }
    }

    public CurrencyRate getCurrencyRate() {
        try {
            String currencyRateJson = redisTemplate.opsForValue().get(REDIS_KEY_CURRENCY_RATE);
            return objectMapper.readValue(currencyRateJson, CurrencyRate.class);

        } catch (RuntimeException | JsonProcessingException e) {
            log.error("Error while saving to Redis", e);
            throw new CurrencyRateException("Fail to get currency rates, try again later");
        }
    }
}
