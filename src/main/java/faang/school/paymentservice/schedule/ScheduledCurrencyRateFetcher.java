package faang.school.paymentservice.schedule;

import faang.school.paymentservice.config.currencyRate.CurrencyRateFetcherConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;

@RequiredArgsConstructor
@Slf4j
public class ScheduledCurrencyRateFetcher {
    private final CurrencyRateFetcherConfig currencyRateFetcherConfig;

    @Scheduled(cron = "${schedule.current-rate-cron}")
    public void scheduleRate(){
        log.info("Launch scheduled current rate fetcher");
        try {
            currencyRateFetcherConfig.getCurrentRate();
        }catch (Exception e) {
            log.error("Error to current rate fetcher", e);
        }
    }
}