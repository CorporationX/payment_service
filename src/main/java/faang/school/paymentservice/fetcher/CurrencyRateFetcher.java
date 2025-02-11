package faang.school.paymentservice.fetcher;

import faang.school.paymentservice.service.CurrencyService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CurrencyRateFetcher {
    private final CurrencyService currencyService;

    @Scheduled(cron = "${currency.fetch.cron}")
    public void getExchangeRate() {
        currencyService.fetchCurrencyRates();
    }
}
