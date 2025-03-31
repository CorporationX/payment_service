package faang.school.paymentservice.config;

import faang.school.paymentservice.service.CurrencyService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CurrencyRateFetcher {
    private final CurrencyService currencyService;

@Scheduled(cron = "${currency.rate.update.cron}")
    public void fetchCurrencyRates(){
    currencyService.updateCurrencyRates();
    }
}
