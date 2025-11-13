package faang.school.paymentservice.store.currencyRate;

import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicReference;

@Component
public class CurrencyRateStore {
    private final AtomicReference<CurrencySnapshot> current = new AtomicReference<>();

    public void update(CurrencySnapshot snapshot) {
        current.set(snapshot);
    }
}