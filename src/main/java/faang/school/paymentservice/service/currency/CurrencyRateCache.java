package faang.school.paymentservice.service.currency;

import faang.school.paymentservice.dto.Currency;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentMap;

@Component
public class CurrencyRateCache {
    /**
     Тут нужно бы добавить, чтобы получать значение карренси могли все потоки.А обновление могло происходить, только
     если никто не читает значение из поля и наоборот.
     Но не знаю как это сделать
    */
    @Value("${currency-rate-fetcher.base_currency}")
    @Getter
    private String baseCurrency;
    @Setter
    private ConcurrentMap<Currency, Double> currencyRate;

    public Double getCurrencyRate(Currency currency) {
        return currencyRate.get(currency);
    }

    public ConcurrentMap<Currency, Double> getAllCurrencyRates() {
        return currencyRate;
    }

}
