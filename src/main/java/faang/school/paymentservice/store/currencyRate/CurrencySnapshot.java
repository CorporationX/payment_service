package faang.school.paymentservice.store.currencyRate;

import java.time.Instant;
import java.util.Map;

public record CurrencySnapshot (Instant fetchedAt,
        String baseCurrency,
        Map<String, Double> rates) {

    public CurrencySnapshot(Instant fetchedAt,
                            String baseCurrency,
                            Map<String, Double> rates) {
        this.fetchedAt = fetchedAt;
        this.baseCurrency = baseCurrency;
        this.rates = Map.copyOf(rates);
    }
}
