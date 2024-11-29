package faang.school.paymentservice.service.currency;

import faang.school.paymentservice.dto.RatesDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientRequestException;

@Slf4j
@Service
@RequiredArgsConstructor
public class RetryableCurrencyFetcher {

    private final CurrencyRateFetcher currencyRateFetcher;

    @Retryable(maxAttempts = 3, backoff = @Backoff(delay = 5000))
    public RatesDto fetchDataWithRetry() {
        try {
            return currencyRateFetcher.fetchData();
        } catch (WebClientRequestException e) {
            log.error("WebClientRequestException: {}", e.getMessage());
            throw e;
        }
    }

    @Recover
    public RatesDto failedToGetExchangeRates() {
        log.error("Failed to get exchange rates!");
        return null;
    }
}