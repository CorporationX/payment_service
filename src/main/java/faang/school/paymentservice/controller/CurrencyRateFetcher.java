package faang.school.paymentservice.controller;

import faang.school.paymentservice.dto.Currency;
import faang.school.paymentservice.service.currency.CurrencyService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/currency-rate")
@RequiredArgsConstructor
public class CurrencyRateFetcher {
    private final CurrencyService service;

    @Scheduled(fixedDelayString = "${currency-rate-fetcher.cron}")
    public void updateActualCurrencyRate() {
        service.updateActualCurrencyRate();
    }

    @GetMapping("/health")
    public Map<Currency, Double> checkHealth() {
        return service.getAllCurrencyRates();
    }
}
