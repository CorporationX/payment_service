package faang.school.paymentservice.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class CurrencyRateFetcher {
    private CurrencyService currencyService;

    @Scheduled(cron = "${currency.fetch.cron}")
    public void getExchangeRates() {
        currencyService.getCurrentCurrencyExchangeRate();
        System.out.println("Fetching currency rates...");
    }
}