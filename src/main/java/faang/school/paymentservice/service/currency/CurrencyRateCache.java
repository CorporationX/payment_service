package faang.school.paymentservice.service.currency;

import faang.school.paymentservice.dto.Currency;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Component
public class CurrencyRateCache {
    @Getter
    private LocalDateTime updatedAt;

    @Value("${currency-rate-fetcher.base_currency}")
    @Getter
    private String baseCurrency;

    @Setter
    private Map<Currency, Double> currencyRate = new HashMap<>();


    public Double getCurrencyRate(Currency currency) {
        return currencyRate.get(currency);
    }

    public Map<Currency, Double> getAllCurrencyRates() {
        return this.currencyRate;
    }


    public void updateUpdatedAt() {
        this.updatedAt = LocalDateTime.now();
    }

}
