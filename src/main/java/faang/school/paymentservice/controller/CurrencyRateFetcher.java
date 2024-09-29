package faang.school.paymentservice.controller;

import faang.school.paymentservice.service.CurrencyService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class CurrencyRateFetcher {
    private final CurrencyService service;
    @Scheduled(cron = "${currency-rate-fetcher.cron}")
    public void UpdateActualCurrencyRate() {
        service.UpdateActualCurrencyRate();
    }
}
