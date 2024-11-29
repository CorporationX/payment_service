package faang.school.paymentservice.job;

import faang.school.paymentservice.service.currency.CurrencyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CurrencyRateJob {

    private final CurrencyService currencyService;

    @Scheduled(cron = "${cron.update-rates}")
    public void updateCurrencyRates() {
        currencyService.updateRates();
    }
}