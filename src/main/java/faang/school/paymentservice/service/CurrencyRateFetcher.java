package faang.school.paymentservice.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class CurrencyRateFetcher {
    private final CurrencyService currencyService;

    @PostConstruct
    @Scheduled(cron = "${cron.currency-rate-fetcher}")
    public void runAfterObjectCreated() {
        currencyService.currencyRateFetcher();
    }
}
