package faang.school.paymentservice.service.fetcher;

import faang.school.paymentservice.service.ExchangeService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ExchangeRateFetcher {
    private final ExchangeService exchangeService;

    @Scheduled(cron = "${currency.fetch.cron}")
    public void getExchangeRate() {
        exchangeService.fetchCurrencyRates();
    }
}
