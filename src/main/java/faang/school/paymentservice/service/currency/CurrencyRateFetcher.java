package faang.school.paymentservice.service.currency;

import faang.school.paymentservice.service.CurrencyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
@Slf4j
public class CurrencyRateFetcher {
    private final CurrencyService currencyService;

    @Scheduled(cron = "${app.scheduling.currency-fetch-cron}")
    public void fetchAndCacheLatestRates() {
        log.info("Starting scheduled task");

        currencyService.getLatestRates()
                .subscribe(
                        response -> log.info("Scheduler: success"),
                        error -> log.error("Scheduler: error {}", error.getMessage())
                );
    }
}