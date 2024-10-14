package faang.school.paymentservice.service.exchangerate;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.net.ConnectException;
import java.util.concurrent.TimeoutException;

@Component
@AllArgsConstructor
@Slf4j
public class FetchCurrenctRateScheduler {

    private final CurrencyService currencyService;

    @Scheduled(cron = "${currency.rate.fetch-cron}")
    @Retryable(retryFor = {ConnectException.class, TimeoutException.class}, backoff = @Backoff(delay = 1000, multiplier = 1))
    public void fetchCurrencyRates() {
        log.info("currency rates requested");
        currencyService.updateCurrencyRates();
    }
}
