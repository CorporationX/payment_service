package faang.school.paymentservice.service.currency.implementations;

import faang.school.paymentservice.dto.Currency;
import faang.school.paymentservice.dto.ExchangeRateResponse;
import faang.school.paymentservice.exception.ExchangeRatesException;
import faang.school.paymentservice.service.currency.interfaces.CurrencyService;
import faang.school.paymentservice.service.currency.interfaces.ExchangeRateProvider;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class CurrencyServiceImpl implements CurrencyService {

    private final CacheManager cacheManager;
    private final List<ExchangeRateProvider> exchangeRateProviders;

    @PostConstruct
    public void initCache() {
        log.info("Initializing cache");
        fetchExchangeRates();
        log.info("Cache initialized");
    }

    @Override
    @Retryable(value = {ExchangeRatesException.class}, maxAttempts = 3, backoff = @Backoff(delay = 2000, multiplier = 2))
    @CachePut(value = "rates", key = "'rates'")
    public Map<Currency, BigDecimal> fetchExchangeRates() {
        log.info("Start fetching exchange rates");
        for (ExchangeRateProvider provider : exchangeRateProviders) {
            try {
                ExchangeRateResponse exchangeRateResponse = provider.getExchangeRates();
                Objects.requireNonNull(cacheManager.getCache("base"))
                        .put("base", exchangeRateResponse.getBase());
                log.info("Finish fetching exchange rates: base currency - {}", exchangeRateResponse.getBase());
                log.info("Exchange rates: {}", exchangeRateResponse.getRates());
                return exchangeRateResponse.getRates();
            } catch (Exception e) {
                log.warn("Error fetching exchange rates from provider: {}", provider);
            }
        }
        log.error("Failed to fetch exchange rates from all providers");
        throw new ExchangeRatesException("Failed to fetch exchange rates from all providers");
    }

    @Override
    @Cacheable(value = "rates", key = "'rates'")
    public Map<Currency, BigDecimal> getExchangeRates() {
        return fetchExchangeRates();
    }

    @Override
    @Cacheable(value = "base", key = "'base'")
    public Currency getBaseCurrency() {
        return fetchExchangeRates().entrySet().stream()
                .filter(e -> e.getValue().compareTo(BigDecimal.ONE) == 0)
                .map(Map.Entry::getKey)
                .findFirst().orElseThrow(() -> {
                    log.error("Base currency not found");
                    return new ExchangeRatesException("Base currency not found");
                });

    }
}
