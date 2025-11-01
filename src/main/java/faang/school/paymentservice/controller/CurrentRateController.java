package faang.school.paymentservice.controller;


import faang.school.paymentservice.service.CurrencyServiceImpl;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Hidden
@RestController("api/v1")
@RequiredArgsConstructor
public class CurrentRateController {
    private final CurrencyServiceImpl currencyService;

    @GetMapping("/currency")
    public String getCurrency() {
        return currencyService.getCurrencyRate();
    }
}