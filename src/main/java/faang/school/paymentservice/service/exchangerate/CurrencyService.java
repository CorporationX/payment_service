package faang.school.paymentservice.service.exchangerate;

import faang.school.paymentservice.client.CurrencyRatesClient;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@AllArgsConstructor
@Slf4j
public class CurrencyService {

    private final CurrencyRatesClient currencyRatesClient;
    private final Map<String, Double> currencyRates = new ConcurrentHashMap<>();


    public void updateCurrencyRates() {
        currencyRatesClient.fetchRates().subscribe(currencyRates::putAll);
    }

    public Double getRate(String currency) {
        return currencyRates.get(currency);
    }
}
