package faang.school.paymentservice.controller;

import faang.school.paymentservice.dto.CurrencyRateDto;
import faang.school.paymentservice.service.currency.CurrencyService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/currency-rate")
@RequiredArgsConstructor
public class CurrencyRateController {
    private final CurrencyService service;

    @GetMapping("/health")
    public CurrencyRateDto checkHealth() {
        return service.checkHealth();
    }
}
