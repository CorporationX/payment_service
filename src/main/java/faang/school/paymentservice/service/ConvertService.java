package faang.school.paymentservice.service;

import faang.school.paymentservice.dto.Currency;
import faang.school.paymentservice.dto.Rate;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ConvertService {
    private final RedisCacheManager manager;
    private final CurrencyService currencyService;

    @Value("${spring.cache.redis.caches.current_rate}")
    private String rateCache;

    public BigDecimal convert(Currency convertable, Currency base, BigDecimal sum) {
        Rate rate = addCommission(convertable, base);
        return sum.multiply(BigDecimal.valueOf(rate.getRates().get(convertable.getName())));
    }

    private Rate addCommission(Currency convertable, Currency base1) {
        Rate rate = (Rate) Objects.requireNonNull(manager.getCache(rateCache))
                .get("current_rate")
                .get();

        if (rate == null) {
            rate = currencyService.updateCurrency();
        }

        Map<String, Double> mapRate = rate.getRates();
        Map<String, Double> newMapRate = new HashMap<>();
        if (Objects.equals(convertable.getName(), rate.getBase())) {
            newMapRate.put(base1.getName(), mapRate.get(base1.getName()) * 0.99);
        } else {
            double baseCur = mapRate.get(convertable.getName());
            double convertableCur = mapRate.get(base1.getName());

            newMapRate.put(base1.getName(), convertableCur/baseCur);
        }
        rate.setRates(newMapRate);

        return rate;
    }
}
