package faang.school.paymentservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CurrencyRateFetcher {
    private final CurrencyService currencyService;

    @Scheduled(cron = "${cron.expression}")
    public void fetchCurrencyRate() {
        currencyService.fetchAndStoreCurrencyRates();
    }
}
