package faang.school.paymentservice.controller;

import faang.school.paymentservice.dto.Currency;
import faang.school.paymentservice.dto.PaymentRequest;
import faang.school.paymentservice.dto.PaymentResponse;
import faang.school.paymentservice.dto.PaymentStatus;
import faang.school.paymentservice.service.converter.CurrencyConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Random;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("${controller.payment.url}")
public class PaymentController {

    private final CurrencyConverter currencyConverter;

    @Value("${default.target.currency}")
    private Currency targetCurrency;

    @PostMapping("/payment")
    public ResponseEntity<PaymentResponse> sendPayment(@RequestBody @Validated PaymentRequest dto) {
        try {
            BigDecimal convertedAmount = currencyConverter.convert(dto.currency(), targetCurrency, dto.amount());
            DecimalFormat decimalFormat = new DecimalFormat("0.00");
            String formattedSum = decimalFormat.format(convertedAmount);
            int verificationCode = new Random().nextInt(1000, 10000);
            String message = String.format("Dear friend! Thank you for your purchase! " +
                                           "Your payment of %s %s was accepted.",
                    formattedSum, targetCurrency.name());

            return ResponseEntity.ok(new PaymentResponse(
                    PaymentStatus.SUCCESS,
                    verificationCode,
                    dto.paymentNumber(),
                    convertedAmount,
                    targetCurrency,
                    message)
            );
        } catch (Exception e) {
            log.error("Error converting currency", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new PaymentResponse(
                            PaymentStatus.FAILURE,
                            0,
                            dto.paymentNumber(),
                            dto.amount(),
                            dto.currency(),
                            "Error processing payment"
                    ));
        }
    }

    @GetMapping("/convert/{amount}/{fromCurrency}/{toCurrency}")
    public BigDecimal getSum(@PathVariable("amount") BigDecimal amount,
                             @PathVariable("fromCurrency") Currency fromCurrency,
                             @PathVariable("toCurrency") Currency toCurrency) {
        return currencyConverter.convert(fromCurrency, toCurrency, amount);
    }
}
