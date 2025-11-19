package faang.school.paymentservice.service.currency;

import faang.school.paymentservice.config.currencyRate.CurrencyRateFetcherConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@RequiredArgsConstructor
@Service
public class CurrencyServiceImpl implements CurrencyService {
    private final MapCurrencyService mapCurrencyService;

    @Override
    public String getCurrencyRate() {
        Map<String, Double> rates = mapCurrencyService.mapCurrentRate();
        StringBuilder result = new StringBuilder();
        rates.forEach((key, value) -> result.append(String.format("%s-%f\n", key, value)));
        return result.toString().trim();
    }
}