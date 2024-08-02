package faang.school.paymentservice.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class CurrencyRateFetcher {
    private CurrencyConverterService currencyConverterService;

    @Scheduled(cron = "${currency.fetch.cron}")
    public void getExchangeRates() {
        currencyConverterService.getCurrentCurrencyExchangeRate();
        System.out.println("Fetching currency rates...");
    }
}
