package faang.school.paymentservice.controller;

import faang.school.paymentservice.config.CurrencyRateFetcher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequiredArgsConstructor
@RequestMapping("/test")
@Slf4j
public class testController {
    private final CurrencyRateFetcher currencyRateFetcher;

    @PostMapping
    public void qwe(){
        currencyRateFetcher.saveCurrency();
    }

}
