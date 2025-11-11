package faang.school.paymentservice.service.currency;

import faang.school.paymentservice.client.ExternalCurrencyClient;
import faang.school.paymentservice.dto.ExchangeRatesResponse;
import faang.school.paymentservice.exception.CurrencyApiException;
import faang.school.paymentservice.store.currencyRate.CurrencyRateStore;
import faang.school.paymentservice.store.currencyRate.CurrencySnapshot;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Slf4j
@Service
@AllArgsConstructor
public class CurrencyServiceImpl implements CurrencyService {
    private final ExternalCurrencyClient currencyClient;
    private final CurrencyRateStore currencyStore;

    @Override
    @Retry(name = "currencyRetry", fallbackMethod = "fallbackRates")
    public void refreshRates(String baseCurrency) {
        log.info("Request for update rate for a base currency = {}", baseCurrency);
        ExchangeRatesResponse response = currencyClient.fetchLatestRates(baseCurrency);

        if (response == null || response.rates() == null) {
            log.warn("Can't get new rates - keeping old ones");
            throw new CurrencyApiException("Empty external Api response");
        }

        CurrencySnapshot snapshot = new CurrencySnapshot(Instant.now(),
                response.baseCurrency(),
                response.rates());
        currencyStore.update(snapshot);
        log.info("Rates have updated: base currency = {}, ratesCount = {}, fetchedAt={}",
                response.baseCurrency(),
                response.rates().size(),
                snapshot.fetchedAt());
    }

    public void fallbackRates(String baseCurrency, Throwable exception) {
        log.warn("Retry failed for base currency = {}, reason: {}", baseCurrency, exception.getMessage());
    }
}
