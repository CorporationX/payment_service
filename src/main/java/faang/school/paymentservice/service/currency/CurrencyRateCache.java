package faang.school.paymentservice.service.currency;

import faang.school.paymentservice.dto.Currency;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;

@Component
public class CurrencyRateCache {
    @Value("${currency-rate-fetcher.base_currency}")
    @Getter
    private String baseCurrency;
    private Map<Currency, Double> currencyRate = new HashMap<>();
    @Autowired
    @Setter
    @Qualifier("currencyRateCacheLock")
    private ReadWriteLock lock;

    public Double getCurrencyRate(Currency currency) {
        lock.readLock().lock();
        Double rate =  currencyRate.get(currency);
        lock.readLock().unlock();
        return rate;
    }

    public Map<Currency, Double> getAllCurrencyRates() {
            lock.readLock().lock();
            Map<Currency, Double> curRate = this.currencyRate;
            lock.readLock().unlock();
        return curRate;
    }

    public void setCurrencyRate(Map<Currency, Double> currencyRate) {
        lock.writeLock().lock();
        this.currencyRate = currencyRate;
        lock.writeLock().unlock();
    }

}
