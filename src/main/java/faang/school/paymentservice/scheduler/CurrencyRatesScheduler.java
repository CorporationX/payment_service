package faang.school.paymentservice.scheduler;

import faang.school.paymentservice.service.CurrencyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CurrencyRatesScheduler {
    private final CurrencyService currencyService;

    @Scheduled(cron = "${external.currency.scheduler.cron}")
    public void refreshRates() {
        log.info("Scheduled job started: refreshing currency rates");
        currencyService.updateRates()
                .doOnError(error -> log.error("Scheduled job failed", error))
                .subscribe();
    }
}
