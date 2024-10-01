package faang.school.paymentservice.controller;

import faang.school.paymentservice.dto.Currency;
import faang.school.paymentservice.dto.PaymentRequest;
import faang.school.paymentservice.dto.PaymentResponse;
import faang.school.paymentservice.dto.PaymentStatus;
import faang.school.paymentservice.service.CurrencyExchangeService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Validated
public class PaymentController {
    private final CurrencyExchangeService currencyExchangeService;

    @Value("${currency.exchange.appId}")
    String appId;

    @PostMapping("/payment")
    public ResponseEntity<PaymentResponse> sendPayment(
            @RequestBody @Validated PaymentRequest dto, @RequestParam Currency toCurrency) {
        int verificationCode = new Random().nextInt(1000, 10000);
        return ResponseEntity.ok(new PaymentResponse(
                PaymentStatus.SUCCESS,
                verificationCode,
                dto.paymentNumber(),
                dto.amount(),
                dto.currency(),
                currencyExchangeService.getMessage(dto, toCurrency))
        );
    }
}