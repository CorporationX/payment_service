package faang.school.paymentservice.controller;

import faang.school.paymentservice.dto.Currency;
import faang.school.paymentservice.dto.CurrencyConverterDto;
import faang.school.paymentservice.dto.PaymentRequest;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Random;
import faang.school.paymentservice.dto.PaymentResponse;
import faang.school.paymentservice.dto.PaymentStatus;
import faang.school.paymentservice.service.currencyconverter.CurrencyConverterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PaymentController {
    private final CurrencyConverterService converterService;

    @PostMapping("/payment")
    public ResponseEntity<PaymentResponse> sendPayment(@RequestBody @Validated PaymentRequest dto) {
        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        Currency currency = dto.currency();
        BigDecimal amount = dto.amount();
        if (currency != Currency.USD) {
            CurrencyConverterDto converterDto = CurrencyConverterDto.builder()
                    .transmittedCurrency(currency)
                    .transmittedSum(amount)
                    .receivedCurrency(Currency.USD)
                    .build();
            CurrencyConverterDto receivedConverterDto = converterService.convert(converterDto);
            amount = receivedConverterDto.getTotalSum();
            dto = new PaymentRequest(dto.paymentNumber(),amount, dto.currency());
        }
        String formattedSum = decimalFormat.format(amount);
        int verificationCode = new Random().nextInt(1000, 10000);
        String message = String.format("Dear friend! Thank you for your purchase! " +
                        "Your payment on %s %s was accepted.",
                formattedSum, currency.name());

        return ResponseEntity.ok(new PaymentResponse(
                PaymentStatus.SUCCESS,
                verificationCode,
                dto.paymentNumber(),
                dto.amount(),
                dto.currency(),
                message)
        );
    }
}
