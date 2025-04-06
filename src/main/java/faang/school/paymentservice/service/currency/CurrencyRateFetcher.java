package faang.school.paymentservice.service.currency;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CurrencyRateFetcher {
    private final CurrencyService currencyService;

    @Scheduled(cron = "${app.corn.schedule}")
    public void updateCurrencyRates() {
        currencyService.fetchCurrencyRates();
    }
}
