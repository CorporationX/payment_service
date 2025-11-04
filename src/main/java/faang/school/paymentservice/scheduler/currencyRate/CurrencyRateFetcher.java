package faang.school.paymentservice.scheduler.currencyRate;

import faang.school.paymentservice.service.currency.CurrencyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CurrencyRateFetcher {
    private final CurrencyService currencyService;
    private final String base;

    public CurrencyRateFetcher(CurrencyService currencyService,
                             @Value("${currency.fetch.base}") String base) {
        this.currencyService = currencyService;
        this.base = base;
    }

    @Scheduled(cron = "${currency.fetch.cron}", zone = "Asia/Almaty")
    public void fetchAndStoreRates() {
        log.info("Fetch and store rates for base = {}", base);
        currencyService.refreshRates(base);
    }
}
