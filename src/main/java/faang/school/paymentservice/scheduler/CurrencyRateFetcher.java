package faang.school.paymentservice.scheduler;

import faang.school.paymentservice.sevice.CurrencyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CurrencyRateFetcher {
    private final CurrencyService currencyService;

    @Scheduled(cron = "${currency.rate.cron}")
    public void updateCurrencyRates() {
        try {
            log.info("Scheduled currency rate fetcher started");
            currencyService.fetchCurrencyRates();
            log.info("Scheduled currency rate fetcher completed");
        } catch (Exception e) {
            log.error("Scheduled currency rates fetcher is failed. Cause: ", e);
        }
    }
}
