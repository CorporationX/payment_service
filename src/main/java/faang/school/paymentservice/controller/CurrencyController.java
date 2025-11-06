package faang.school.paymentservice.controller;

import faang.school.paymentservice.dto.Currency;
import faang.school.paymentservice.service.currencyrate.CurrencyRateDto;
import faang.school.paymentservice.service.currencyrate.CurrencyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("api/v1/currency-rates")
@RequiredArgsConstructor
public class CurrencyController {

    private final CurrencyService currencyRateService;

    @GetMapping
    public ResponseEntity<Map<LocalDateTime, CurrencyRateDto>> getCurrencyRate() {
        return ResponseEntity.of(Optional.ofNullable(
                currencyRateService.getCurrencyRates()));
    }

    @GetMapping("/latest")
    public ResponseEntity<CurrencyRateDto> getLatestRates() {
        return ResponseEntity.of(Optional.ofNullable(
                currencyRateService.getLatestRate()));
    }

    @GetMapping("/{currency}")
    public ResponseEntity<Double> getCurrencyRateByCurrency(Currency currency) {
        return ResponseEntity.of(Optional.ofNullable(
                currencyRateService.getCurrencyRateByCurrency(currency)
        ));
    }
}