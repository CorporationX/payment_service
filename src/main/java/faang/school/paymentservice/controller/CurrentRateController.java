package faang.school.paymentservice.controller;


import faang.school.paymentservice.service.currency.CurrencyService;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@Hidden
@RequestMapping("api/v1/currency")
@RestController
@RequiredArgsConstructor
public class CurrentRateController {
    private final CurrencyService currencyService;

    @GetMapping
    public String getCurrency() {
        return currencyService.getCurrencyRate();
    }

    @PostMapping
    public void clearCache() {
        currencyService.clearRates();
    }

    @GetMapping("/{currency}")
    public BigDecimal getRate(@PathVariable String currency) {
       return currencyService.getRate(currency);
    }
}