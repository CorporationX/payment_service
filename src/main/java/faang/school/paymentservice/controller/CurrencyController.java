package faang.school.paymentservice.controller;

import faang.school.paymentservice.dto.payment.ExchangeRates;
import faang.school.paymentservice.service.ExchangeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/currency")
@RequiredArgsConstructor
public class CurrencyController {
    private final ExchangeService exchangeService;

    @GetMapping
    public ResponseEntity<ExchangeRates> getCurrentCurrencyExchangeRate() {
        return ResponseEntity.status(HttpStatus.OK).body(exchangeService.getCurrencyRates());
    }
}
