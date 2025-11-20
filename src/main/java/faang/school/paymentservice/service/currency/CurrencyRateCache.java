package faang.school.paymentservice.service.currency;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import faang.school.paymentservice.config.currencyRate.CurrencyRateFetcherConfig;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.TimeUnit;


@Slf4j
@RequiredArgsConstructor
@Component
public class CurrencyRateCache {
    @Value("${current-rate.maximum-size}")
    private int maximumSize;
    @Value("${current-rate.expire-after-write}")
    private int expireAfterWriteMinutes;
    @Value("${current-rate.refresh-after-write}")
    private int refreshAfterWriteMinutes;
    private final CurrencyRateFetcherConfig fetcher;
    private LoadingCache<String, Map<String, BigDecimal>> ratesCache;


    @PostConstruct
    private void initCache() {
        this.ratesCache = Caffeine.newBuilder()
                .maximumSize(maximumSize)
                .expireAfterWrite(expireAfterWriteMinutes, TimeUnit.MINUTES)
                .refreshAfterWrite(refreshAfterWriteMinutes, TimeUnit.MINUTES)
                .recordStats()
                .build(key -> fetcher.getCurrentRate());
        ratesCache.get("ALL_RATES");
    }

    public BigDecimal getRates(String currency) {
        String code = currency.toUpperCase();
        return ratesCache.get("ALL_RATES")
                .getOrDefault(code, BigDecimal.ONE);
    }

    public Map<String, BigDecimal> getAllRates() {
        return ratesCache.get("ALL_RATES");
    }

    public void invalidateAll() {
        ratesCache.invalidateAll();
        log.info("Currency rate cache is clear");
    }

}