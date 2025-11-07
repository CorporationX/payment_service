package faang.school.paymentservice.service.currencyrate;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CurrencyRateFetcher {

    private final CurrencyService currencyService;

    @Scheduled(cron = "${scheduled.daily-task}")
    public void fetchCurrencyRate() {
        currencyService.fetchCurrencyRate();

    }
}
