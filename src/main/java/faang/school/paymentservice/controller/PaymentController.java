package faang.school.paymentservice.controller;

import faang.school.paymentservice.dto.PaymentRequest;
import faang.school.paymentservice.dto.PaymentResponse;
import faang.school.paymentservice.dto.PaymentStatus;
import faang.school.paymentservice.exception.CurrencyNotFoundException;
import faang.school.paymentservice.service.CurrencyConverterService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.text.DecimalFormat;
import java.util.Random;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    private final CurrencyConverterService currencyConverterService;

    public PaymentController(CurrencyConverterService currencyConverterService) {
        this.currencyConverterService = currencyConverterService;
    }

    @PostMapping
    public ResponseEntity<?> sendPayment(@Valid @RequestBody PaymentRequest paymentRequest) {
        try {
            double convertedAmount = currencyConverterService.convert(paymentRequest.fromCurrency().name(), paymentRequest.toCurrency().name(), paymentRequest.amount().doubleValue());
            return ResponseEntity.ok("Платеж успешен. Сумма с учетом комиссии: " + convertedAmount);
        } catch (CurrencyNotFoundException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}