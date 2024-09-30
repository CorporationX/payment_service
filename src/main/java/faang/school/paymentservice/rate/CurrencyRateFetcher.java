package faang.school.paymentservice.rate;

import faang.school.paymentservice.service.CurrencyService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CurrencyRateFetcher {
    private final CurrencyService service;

    @Scheduled(cron = "0 0 * * *")
    public void update() {

    }
}
