package faang.school.paymentservice.controller;

import faang.school.paymentservice.config.ExchangeCurrencyConfig;
import faang.school.paymentservice.dto.ConvertCurrencyResponse;
import faang.school.paymentservice.dto.Currency;
import faang.school.paymentservice.dto.PaymentRequest;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Random;
import faang.school.paymentservice.dto.PaymentResponse;
import faang.school.paymentservice.dto.PaymentStatus;
import faang.school.paymentservice.service.PaymentService;
import jakarta.validation.Valid;
import jakarta.websocket.server.PathParam;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class PaymentController {
    private final PaymentService paymentService;
    private final ExchangeCurrencyConfig currencyConfig;

    @PostMapping("/payment")
    public ResponseEntity<PaymentResponse> sendPayment(@RequestBody @Valid PaymentRequest dto) {
        String formattedSum = decimalFormat(dto.amount());

        String message = String.format("Dear friend! Thank you for your purchase! " +
                        "Your payment on %s %s was accepted.",
                formattedSum, dto.currency().name());

        return ResponseEntity.ok(new PaymentResponse(
                PaymentStatus.SUCCESS,
                getVerificationCode(),
                dto.paymentNumber(),
                dto.amount(),
                dto.currency(),
                message)
        );
    }

    @GetMapping("/convert/{value}/{from}/{to}")
    public ResponseEntity<ConvertCurrencyResponse> convertCurrency(@PathVariable("value") BigDecimal amount,
                                                                   @PathVariable("from") Currency currencyFrom,
                                                                   @PathVariable("to") Currency currencyTo) {

        BigDecimal convertedAmount = paymentService.convert(amount, currencyFrom, currencyTo);

        String message = String.format(
                "Your amount %s %s converted to %s %s, commission %f%%",
                decimalFormat(amount), currencyFrom,
                decimalFormat(convertedAmount), currencyTo,
                currencyConfig.commission());

        return ResponseEntity.ok(new ConvertCurrencyResponse(
                getVerificationCode(),
                amount, currencyFrom,
                convertedAmount, currencyTo,
                message)
        );
    }

    private String decimalFormat(BigDecimal amount) {
        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        return decimalFormat.format(amount);
    }

    private int getVerificationCode() {
        return new Random().nextInt(1000, 10000);
    }
}
