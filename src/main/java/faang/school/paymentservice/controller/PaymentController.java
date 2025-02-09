package faang.school.paymentservice.controller;

import faang.school.paymentservice.config.CurrencyExchangeConfig;
import faang.school.paymentservice.dto.*;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Random;

import faang.school.paymentservice.service.ConverterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PaymentController {
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("0.00");
    private static final String CONVERT_MONEY_MESS = "You convert %s %s to %s %S with commission %f%%";
    private static final String PAYMENT_MESSAGE = "payment on %s %s was accepted";

    private final CurrencyExchangeConfig exchangeConfig;
    private final ConverterService converterService;

    @PostMapping("payment")
    public CurrencyExchangeResponse getCurrencyExchangeResponse() {
        return converterService.getCurrentCurrencyExchangeRate();
    }

    @PostMapping("/payment")
    public ResponseEntity<PaymentResponse> sendPayment(@RequestBody @Validated PaymentRequest dto) {
        String message = String.format(
                PAYMENT_MESSAGE,
                DECIMAL_FORMAT.format(dto.amount()),
                dto.currency()
        );

        return ResponseEntity.ok(new PaymentResponse(
                PaymentStatus.SUCCESS,
                getVerificationCode(),
                dto.paymentNumber(),
                dto.amount(),
                dto.currency(),
                message)
        );
    }

    public ResponseEntity<PaymentResponse> exchangeCurrency(@RequestBody @Validated PaymentRequest dto,
                                                            @RequestParam Currency targetCurrency) {
        BigDecimal newAmount = converterService.convertWithCommission(dto, targetCurrency);

        String message = String.format(
                CONVERT_MONEY_MESS,
                DECIMAL_FORMAT.format(dto.amount()),
                dto.currency(),
                DECIMAL_FORMAT.format(newAmount),
                targetCurrency,
                exchangeConfig.commission()
        );

        return ResponseEntity.ok(new PaymentResponse(
                PaymentStatus.SUCCESS,
                getVerificationCode(),
                dto.paymentNumber(),
                newAmount,
                targetCurrency,
                message)
        );
    }

    private int getVerificationCode() {
        return new Random().nextInt(1000, 10000);
    }
}
