package faang.school.paymentservice.controller;

import faang.school.paymentservice.service.currency.CurrencyService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
public class CurrencyController {
    private final CurrencyService currencyService;

    @GetMapping("/currency/")
    public void getCourses() {
        currencyService.updateRates();
    }

    @GetMapping("/currency/conversion/EUR/{currency}/{sum}")
    public String convert(@PathVariable String currency, @PathVariable Long sum) {
        return sum + " EUR = " + currencyService.convert(currency, sum) + " " + currency;
    }
}
