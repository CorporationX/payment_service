package faang.school.paymentservice.service.currency;

import faang.school.paymentservice.dto.Currency;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentMap;

@Component
@Setter
public class CurrencyUsdRateCache {
    /**
     Тут нужно бы добавить, чтобы получать значение карренси могли все потоки.А обновление могло происходить, только
     если никто не читает значение из поля и наоборот.
     Но не знаю как это сделать
    */
    private ConcurrentMap<Currency, Double> currencyUsdRate;

    public Double getCurrencyUsdRate(Currency currency) {
        return currencyUsdRate.get(currency);
    }

    public ConcurrentMap<Currency, Double> getAllCurrencyUsdRates() {
        return currencyUsdRate;
    }
}
