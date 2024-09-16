package faang.school.paymentservice.controller;

import faang.school.paymentservice.dto.PaymentRequest;

import faang.school.paymentservice.dto.PaymentResponse;
import faang.school.paymentservice.service.rates.CurrencyConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PaymentController {
    private final CurrencyConverter currencyConverter;

    @PostMapping("/payment")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public PaymentResponse sendPayment(@RequestBody @Validated PaymentRequest dto) {
        return currencyConverter.sendPayment(dto);
    }
}
