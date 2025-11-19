package faang.school.paymentservice.service.currency;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class CurrencyServiceImpl implements CurrencyService {
    private final CurrencyRateCache currencyRateCache;

    @Override
    public String getCurrencyRate() {
        Map<String, BigDecimal> rates = currencyRateCache.getAllRates();
        StringBuilder result = new StringBuilder();
        rates.forEach((key, value) -> result.append(String.format("%s-%f\n", key, value)));
        return result.toString().trim();
    }

    @Override
    public void clearRates() {
        currencyRateCache.invalidateAll();
    }
}