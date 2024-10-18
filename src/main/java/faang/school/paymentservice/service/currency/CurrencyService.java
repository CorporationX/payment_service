package faang.school.paymentservice.service.currency;


import faang.school.paymentservice.client.api.CurrencyApiClient;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
@AllArgsConstructor
public class CurrencyService {
    private final CurrencyRedisService currencyRedisService;
    private final CurrencyApiClient currencyApiClient;

    public void updateCurrencyRates() {
        currencyApiClient.updateCurrencyRates()
                .doOnNext(currencyRedisService::saveCurrencyRates)
                .subscribe();
    }

    public Map<String, Object> getCurrencyRates() {
        return currencyRedisService.getAllCurrencyRates();
    }
}
