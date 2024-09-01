package faang.school.paymentservice.scheduler;

import faang.school.paymentservice.service.CurrencyRateService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @author Evgenii Malkov
 */
@Component
@RequiredArgsConstructor
public class CurrencyRateFetcher {

    private final CurrencyRateService currencyRateService;

    @PostConstruct
    public void onStartup() {
        fetchCurrencyRate();
    }

    @Scheduled(cron = "${currency.rate.scheduler.cron}")
    public void fetchCurrencyRate() {
        currencyRateService.fetchRates();
    }
}
