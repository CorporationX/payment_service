package faang.school.paymentservice.service.currency;

import faang.school.paymentservice.dto.ShortCurrency;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.EnumSet;
import java.util.Objects;

import static faang.school.paymentservice.utils.MessageConstants.NO_INFO_IN_EXCHANGE_RATE_CACHE_FOR_KEY;

@Slf4j
@RequiredArgsConstructor
@Service
public class CurrencyCacheService {

    private final RedisTemplate<String, Object> redisTemplate;

    public void saveRate(String key, BigDecimal rate) {
        redisTemplate.opsForValue().set(key, rate);
    }

    public BigDecimal getRate(String key) {
        Object val = redisTemplate.opsForValue().get(key);
        if (val == null) {
            log.error(String.format(NO_INFO_IN_EXCHANGE_RATE_CACHE_FOR_KEY, key));
            throw new RuntimeException(String.format(NO_INFO_IN_EXCHANGE_RATE_CACHE_FOR_KEY, key));
        }
        return (BigDecimal) val;
    }

    public void deleteRate(String key) {
        redisTemplate.delete(key);
    }

    public void clearRateCache(EnumSet<ShortCurrency> currencies) {
        currencies.forEach(from -> currencies.stream()
                .filter(to -> !Objects.equals(from, to))
                .forEach(to -> deleteRate(from.name() + to.name()))
        );
    }

}
