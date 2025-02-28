package faang.school.paymentservice.service.fetcher;

import faang.school.paymentservice.service.ExchangeService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CurrencyRateFetcher {
    private final ExchangeService currencyService;

    @Scheduled(cron = "${currency.fetch.cron}")
    public void getExchangeRate() {
        currencyService.fetchCurrencyRates();
    }
}
