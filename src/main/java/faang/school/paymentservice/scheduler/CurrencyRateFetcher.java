package faang.school.paymentservice.scheduler;

import faang.school.paymentservice.service.currency.CurrencyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CurrencyRateFetcher {
    private final CurrencyService currencyService;

    @Scheduled(cron = "${scheduler.cron}", zone = "${scheduler.zone}")
    public void fetchExchangeRates() {
        currencyService.getExchangeRates();
        log.info("The exchange rate information update was successful.");
    }
}
