package faang.school.paymentservice.service.currency;

import faang.school.paymentservice.dto.Currency;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Component
public class CurrencyRateCache {
    @Getter
    @Setter
    private String date;

    @Value("${currency-rate-fetcher.base_currency}")
    @Getter
    private String baseCurrency;
    private Map<Currency, Double> currencyRate = new HashMap<>();

    @Setter
    private ReadWriteLock lock = new ReentrantReadWriteLock();

    public Double getCurrencyRate(Currency currency) {
        try {
            lock.readLock().lock();
            return currencyRate.get(currency);
        } finally {
            lock.readLock().unlock();
        }
    }

    public Map<Currency, Double> getAllCurrencyRates() {
        try {
            lock.readLock().lock();
            return this.currencyRate;
        } finally {
            lock.readLock().unlock();
        }
    }

    public void setCurrencyRate(Map<Currency, Double> currencyRate) {
        try {
            lock.writeLock().lock();
            this.currencyRate = currencyRate;
        } finally {
            lock.writeLock().unlock();
        }
    }

}
