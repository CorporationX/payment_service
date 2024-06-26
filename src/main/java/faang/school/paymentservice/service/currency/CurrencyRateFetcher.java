package faang.school.paymentservice.service.currency;

import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CurrencyRateFetcher {
    private final CurrencyConverterService service;
    
    @Scheduled(cron = "${currency.exchange.refresh_cron}")
    @Retryable(maxAttempts = 5, backoff = @Backoff(delay = 5000))
    public void refreshCurrencyExchangeRate() {
        service.getCurrentCurrencyExchangeRate();
    }
}
