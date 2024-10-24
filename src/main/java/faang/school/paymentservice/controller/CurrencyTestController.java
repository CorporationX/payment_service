package faang.school.paymentservice.controller;

import faang.school.paymentservice.dto.Rate;
import faang.school.paymentservice.service.CurrencyServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/test/rate")
public class CurrencyTestController {
    private final CurrencyServiceImpl service;
    @GetMapping("/currency")
    Rate peek() {
        return service.updateCurrency();
    }
}
