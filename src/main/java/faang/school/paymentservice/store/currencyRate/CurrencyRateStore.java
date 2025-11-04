package faang.school.paymentservice.store.currencyRate;

import java.util.concurrent.atomic.AtomicReference;

public class CurrencyRateStore {
    private final AtomicReference<CurrencySnapshot> current = new AtomicReference<>();

    public void update(CurrencySnapshot snapshot) {
        current.set(snapshot);
    }
}