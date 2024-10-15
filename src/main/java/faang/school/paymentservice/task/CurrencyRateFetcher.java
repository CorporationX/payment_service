package faang.school.paymentservice.task;

import faang.school.paymentservice.service.CurrencyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
@Slf4j
public class CurrencyRateFetcher {

    private final CurrencyService currencyService;

    @Scheduled(cron = "${currency-api.cron}")
    public void fetchCurrencyRates() {
        log.info("Scheduled task started: Fetching currency rates");
        currencyService.fetchAndSaveRates();
    }
}
