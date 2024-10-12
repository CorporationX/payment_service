package faang.school.paymentservice.rate;

import faang.school.paymentservice.service.CurrencyServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CurrencyRateFetcher {
    private final CurrencyServiceImpl service;

    @Scheduled(cron = "${cron.every_day}")
    public void update() {
        service.updateCurrency();
    }
}
