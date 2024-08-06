package faang.school.paymentservice.controller;

import faang.school.paymentservice.dto.Currency;
import faang.school.paymentservice.dto.PaymentRequest;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Random;
import faang.school.paymentservice.dto.PaymentResponse;
import faang.school.paymentservice.dto.PaymentStatus;
import faang.school.paymentservice.service.exchange.ExchangeService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
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
    private final ExchangeService exchangeService;
    @Value("${app.currency.base}")
    private String baseCurrency;
    private static final String RESPONSE_WITHOUT_EXCHANGE = "Dear friend! Thank you for your purchase! " +
            "Your payment on %s %s was accepted.";
    private static final String RESPONSE_WITH_EXCHANGE = "Dear friend! Thank you for your purchase! " +
            "Your payment on %s %s converted to %s %s and was accepted.";

    @PostMapping("/payment")
    public ResponseEntity<PaymentResponse> sendPayment(@RequestBody @Validated PaymentRequest dto) {
        BigDecimal amount = dto.amount();
        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        String formattedSum = decimalFormat.format(amount);
        int verificationCode = new Random().nextInt(1000, 10000);
        String message = String.format(RESPONSE_WITHOUT_EXCHANGE, formattedSum, dto.currency().name());

        if (!exchangeService.isCurrencyBase(dto)) {
            amount = exchangeService.getAmountInBaseCurrency(dto);
            String formattedNewSum = decimalFormat.format(amount);
            message = String.format(RESPONSE_WITH_EXCHANGE, formattedSum,
                    dto.currency().name(), formattedNewSum, baseCurrency);
        }

        return ResponseEntity.ok(new PaymentResponse(
                PaymentStatus.SUCCESS,
                verificationCode,
                dto.paymentNumber(),
                amount,
                Currency.valueOf(baseCurrency),
                message)
        );
    }
}
