package faang.school.paymentservice.controller;

import faang.school.paymentservice.service.currency.CurrencyService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;


@RequestMapping("/api/currency-rate")
@RequiredArgsConstructor
@Controller
public class CurrencyRateFetcher {
    private final CurrencyService service;

    @Scheduled(cron = "${currency-rate-fetcher.cron}")
    @PostConstruct
    public void updateActualCurrencyRate() {
        service.updateActualCurrencyRate();
    }

}
