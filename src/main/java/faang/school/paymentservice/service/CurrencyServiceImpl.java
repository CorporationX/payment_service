package faang.school.paymentservice.service;

import faang.school.paymentservice.config.CurrencyRateFetcher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@RequiredArgsConstructor
@Service
public class CurrencyServiceImpl implements CurrencyService{
    private final CurrencyRateFetcher currencyRateFetcher;

    @Override
    public String getCurrencyRate() {
        Map<String, Double> rates = currencyRateFetcher.getMapCurrentRate();
        StringBuilder result = new StringBuilder();
        rates.forEach((key, value) -> result.append(String.format("%s-%f\n", key, value)));
        return result.toString().trim();
    }
}