package faang.school.paymentservice.service.currency;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import faang.school.paymentservice.config.currencyRate.CurrencyRateFetcherConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class CurrencyRateCache {
    private final CurrencyRateFetcherConfig fetcher;
    private final LoadingCache<String, Map<String, BigDecimal>> ratesCache;

    public CurrencyRateCache(CurrencyRateFetcherConfig fetcher,
                             @Value("${current-rate.maximum-size}") int maximumSize,
                             @Value("${current-rate.expire-after-write}") int expireAfterWrite,
                             @Value("${current-rate.refresh-after-write}") int refreshAfterWrite) {

        this.fetcher = fetcher;
        this.ratesCache = Caffeine.newBuilder()
                .maximumSize(maximumSize)
                .expireAfterWrite(expireAfterWrite, TimeUnit.MINUTES)
                .refreshAfterWrite(refreshAfterWrite, TimeUnit.MINUTES)
                .recordStats()
                .build(key -> this.fetcher.getCurrentRate());
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