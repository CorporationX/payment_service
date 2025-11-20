package faang.school.paymentservice.schedule;

import faang.school.paymentservice.service.currency.CurrencyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Slf4j
@Component
public class ScheduledCurrencyRateFetcher {
    private final CurrencyService currencyService;

    @Scheduled(cron = "${schedule.current-rate-cron}")
    public void scheduleRate() {
        log.info("Forced update of exchange rates");
        try {
            currencyService.clearRates();
            log.info("Update completed successfully");
        } catch (Exception e) {
            log.error("Error to current rate fetcher", e);
        }
    }
}