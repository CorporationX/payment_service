package faang.school.paymentservice.controller;

import faang.school.paymentservice.config.properties.CurrencyApiProperties;
import faang.school.paymentservice.dto.Currency;
import faang.school.paymentservice.dto.PaymentRequest;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Random;
import faang.school.paymentservice.dto.PaymentResponse;
import faang.school.paymentservice.dto.PaymentStatus;
import faang.school.paymentservice.service.CurrencyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Slf4j
public class PaymentController {
    private final CurrencyService currencyService;
    private final CurrencyApiProperties currencyApiProperties;

    @PostMapping("/payment")
    public ResponseEntity<PaymentResponse> sendPayment(@RequestBody @Validated PaymentRequest dto) {
        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        String formattedSum = decimalFormat.format(dto.amount());
        int verificationCode = new Random().nextInt(1000, 10000);

        BigDecimal convertedPayment = currencyService.convertToBaseCurrency(dto.amount(), dto.currency().toString());
        log.info("Converted payment to {} {}", convertedPayment, currencyApiProperties.getBaseCurrency());

        String message = String.format("Dear friend! Thank you for your purchase! " +
                        "Your payment on %s %s was accepted and converted to our internal currency %s %s ",
                formattedSum, dto.currency().name(), convertedPayment.toString(), currencyApiProperties.getBaseCurrency().toUpperCase());

        return ResponseEntity.ok(new PaymentResponse(
                PaymentStatus.SUCCESS,
                verificationCode,
                dto.paymentNumber(),
                convertedPayment,
                Currency.valueOf(currencyApiProperties.getBaseCurrency().toUpperCase()),
                message)
        );
    }
}
