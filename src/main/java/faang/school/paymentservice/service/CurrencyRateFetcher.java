package faang.school.paymentservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class CurrencyRateFetcher {
    private final CurrencyService currencyService;

    @Scheduled(cron = "${cron.currency-rate-fetcher}")
    public void runAfterObjectCreated() {
        currencyService.currencyRateFetcher();
    }
}
