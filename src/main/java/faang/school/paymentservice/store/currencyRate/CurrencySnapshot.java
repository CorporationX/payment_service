package faang.school.paymentservice.store.currencyRate;

import lombok.Getter;

import java.time.Instant;
import java.util.Map;

@Getter
public class CurrencySnapshot {
    private Instant fetchedAt;
    private String base;
    private Map<String, Double> rates;

    public CurrencySnapshot(Instant fetchedAt,
                            String base,
                            Map<String, Double> rates) {
        this.fetchedAt = fetchedAt;
        this.base = base;
        this.rates = Map.copyOf(rates);
    }
}
