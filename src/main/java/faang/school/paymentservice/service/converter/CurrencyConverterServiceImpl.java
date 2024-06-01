package faang.school.paymentservice.service.converter;

import faang.school.paymentservice.dto.Currency;
import faang.school.paymentservice.exception.NotFoundException;
import faang.school.paymentservice.service.rates.CurrencyRatesService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class CurrencyConverterServiceImpl implements CurrencyConverterService {

    private final RedisTemplate<String, Double> redisTemplate;
    private final CurrencyRatesService currencyRatesService;

    @Value("${currencyConverter.commission}")
    private BigDecimal commission;

    @Override
    public BigDecimal convert(Currency fromCurrency, Currency toCurrency, BigDecimal amount) {
        Double fromRate = redisTemplate.opsForValue().get(fromCurrency.name());
        Double toRate = redisTemplate.opsForValue().get(toCurrency.name());

        if (fromRate == null || toRate == null) {
            Map<String, Double> rates = currencyRatesService.fetch();
            CompletableFuture<Double> fromRateFuture = getRateAndCacheAsync(fromCurrency, rates);
            CompletableFuture<Double> toRateFuture = getRateAndCacheAsync(toCurrency, rates);

            try {
                fromRate = fromRateFuture.get();
                toRate = toRateFuture.get();
            } catch (Exception e) {
                throw new RuntimeException("Failed to fetch rates", e);
            }
        }

        return BigDecimal.valueOf(toRate / fromRate).multiply(amount).multiply(commission);
    }

    @Async("ratesFetcherExecutorService")
    public CompletableFuture<Double> getRateAndCacheAsync(Currency currency, Map<String, Double> rates) {
        Double rate = rates.get(currency.name());
        if (rate != null) {
            redisTemplate.opsForValue().set(currency.name(), rate);
        } else {
            throw new NotFoundException("Currency rate not found for " + currency.name());
        }
        return CompletableFuture.completedFuture(rate);
    }
}
