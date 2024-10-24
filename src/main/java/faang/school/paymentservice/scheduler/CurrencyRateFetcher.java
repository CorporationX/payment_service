package faang.school.paymentservice.scheduler;

import faang.school.paymentservice.service.currency.CurrencyService;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class CurrencyRateFetcher {
    private final CurrencyService currencyService;

    @Scheduled(cron = "${payment.exchange-rates.api.scheduler.cron}")
    public void fetchCurrencyRate() {
        currencyService.updateCurrencyRates();
    }
}