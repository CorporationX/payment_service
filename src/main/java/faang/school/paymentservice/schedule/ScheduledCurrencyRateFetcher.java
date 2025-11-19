package faang.school.paymentservice.schedule;

import faang.school.paymentservice.service.currency.CurrencyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Slf4j
@Component
public class ScheduledCurrencyRateFetcher {
    private final CurrencyService currencyService;
    @Value("${current-rate.refresh-after-write}")
    private int refreshAfterWrite;

    @Scheduled(cron = "${schedule.current-rate-cron}")
    public void scheduleRate() {
        log.info("Forced update of exchange rates");
        try {
            currencyService.clearRates();
            log.info("Update completed successfully, next update every {} minutes", refreshAfterWrite);
        } catch (Exception e) {
            log.error("Error to current rate fetcher", e);
        }
    }
}