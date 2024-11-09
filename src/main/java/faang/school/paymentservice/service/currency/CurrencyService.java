package faang.school.paymentservice.service.currency;


import faang.school.paymentservice.client.api.CurrencyApiClient;
import faang.school.paymentservice.dto.CurrencyRatesDto;
import faang.school.paymentservice.publisher.currency.CurrencyRedisPublisher;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class CurrencyService {
    private final CurrencyRedisPublisher currencyRedisService;
    private final CurrencyApiClient currencyApiClient;

    public void updateCurrencyRates() {
        currencyApiClient.getAllCurrencyRates()
                .doOnNext(currencyRedisService::saveCurrencyRates)
                .subscribe();
    }

    public CurrencyRatesDto getAllCurrencyRatesFromRedis() {
        return currencyRedisService.getAllCurrencyRates();
    }
}
