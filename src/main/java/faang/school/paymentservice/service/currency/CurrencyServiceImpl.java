package faang.school.paymentservice.service.currency;

import faang.school.paymentservice.client.ExternalCurrencyClient;
import faang.school.paymentservice.dto.ExchangeRatesResponse;
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
    public void refreshRates(String base) {
        log.info("Request for update rate for a base = {}", base);
        ExchangeRatesResponse response = currencyClient.fetchLatestRates(base);

        if (response == null || response.rates() == null) {
            log.warn("Can't get new rates - keeping old ones");
            throw new RuntimeException("Empty external Api response");
        }

        CurrencySnapshot snapshot = new CurrencySnapshot(Instant.now(),
                response.base(),
                response.rates());
        currencyStore.update(snapshot);
        log.info("Rates have updated: base = {}, ratesCount = {}, fetchedAt={}",
                response.base(),
                response.rates().size(),
                snapshot.getFetchedAt());
    }

    public void fallbackRates(String base, Throwable exception) {
        log.warn("Retry failed for base = {}, reason: {}", base, exception.getMessage());
    }
}
