package faang.school.paymentservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Currency;
import java.util.concurrent.ConcurrentMap;

@Service
@RequiredArgsConstructor
public class CurrencyService {
    private final ConcurrentMap<Currency, Double> currencyRubRate;

    public void UpdateActualCurrencyRate() {

    }
}
