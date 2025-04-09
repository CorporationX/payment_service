package faang.school.paymentservice.service.currency;

import faang.school.paymentservice.service.currency.interfaces.CurrencyService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ExchangeRateFetcher {
    private final CurrencyService currencyService;

    @Scheduled(cron = "${currency.cron.expression}")
    public void getExchangeRates() {
        currencyService.fetchExchangeRates();
    }
}
