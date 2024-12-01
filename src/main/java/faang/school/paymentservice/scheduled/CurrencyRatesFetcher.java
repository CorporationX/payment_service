package faang.school.paymentservice.scheduled;

import faang.school.paymentservice.service.CurrencyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CurrencyRatesFetcher {
    private final CurrencyService currencyService;

    @Scheduled(cron = "${scheduler.cron}", zone = "${scheduler.zone}")
    public void fetchExchangeRates() {
        currencyService.getExchangeRates();
        log.info("The exchange rate information update was successful.");
    }
}