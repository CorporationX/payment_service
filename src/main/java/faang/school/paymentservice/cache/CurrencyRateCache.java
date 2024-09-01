package faang.school.paymentservice.cache;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.paymentservice.dto.Currency;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;

/**
 * @author Evgenii Malkov
 */
@Component
@Slf4j
public class CurrencyRateCache extends AbstractCacheManager<BigDecimal> {

    private static final String CURRENCY_RATE_CACHE_KEY = "CURRENCY_RATE";

    public CurrencyRateCache(ObjectMapper mapper, RedisTemplate<String, Object> redisTemplate) {
        super(mapper, redisTemplate);
    }

    public void updateCache(Map<String, BigDecimal> rates) {
        super.put(CURRENCY_RATE_CACHE_KEY, rates);
    }

    public void get(Currency currency) {
        super.get(CURRENCY_RATE_CACHE_KEY, currency.name());
    }
}
