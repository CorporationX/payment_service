package faang.school.paymentservice.controller;

import faang.school.paymentservice.dto.client.ExchangeRatesResponse;
import faang.school.paymentservice.service.rates.CurrencyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class CurrencyController {
    private final CurrencyService currencyService;

    @GetMapping("/currency")
    @ResponseStatus(HttpStatus.OK)
    public ExchangeRatesResponse getCurrentCurrencyExchangeRate() {
        return currencyService.fetchExchangeRates();
    }
}