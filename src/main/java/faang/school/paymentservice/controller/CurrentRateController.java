package faang.school.paymentservice.controller;


import faang.school.paymentservice.service.currency.CurrencyRateCache;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Map;

@Hidden
@RequestMapping("api/v1/currency")
@RestController
@RequiredArgsConstructor
public class CurrentRateController {
    private final CurrencyRateCache cache;

    @GetMapping
    public Map<String, BigDecimal> getCurrency() {
        return cache.getAllRates();
    }

    @PostMapping
    public void clearCache() {
        cache.invalidateAll();
    }

    @GetMapping("/{currency}")
    public BigDecimal getRate(@PathVariable String currency) {
        return cache.getRate(currency);
    }
}