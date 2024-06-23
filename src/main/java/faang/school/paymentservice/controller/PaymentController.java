package faang.school.paymentservice.controller;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Random;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import faang.school.paymentservice.config.currency.CurrencyExchangeConfig;
import faang.school.paymentservice.dto.Currency;
import faang.school.paymentservice.dto.PaymentRequest;
import faang.school.paymentservice.dto.PaymentResponse;
import faang.school.paymentservice.dto.PaymentStatus;
import faang.school.paymentservice.dto.exchange.CurrencyExchangeResponse;
import faang.school.paymentservice.service.CurrencyConverterService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PaymentController {
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("0.00");
    
    private final CurrencyConverterService service;
    private final CurrencyExchangeConfig exchangeConfig;
    
    @GetMapping("currency")
    public CurrencyExchangeResponse getCurrentCurrency() {
        return service.getCurrentCurrencyExchange();
    }

    @PostMapping("payment")
    public ResponseEntity<PaymentResponse> sendPayment(@RequestBody @Validated PaymentRequest dto) {
        String message = String.format(
            "Dear friend! Thank you for your purchase!. Your payment on %s %s was accepted.",
            formatBigDecimal(dto.amount()),
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
    
    @PostMapping("exchange")
    public ResponseEntity<PaymentResponse> exchangeCurrency(@RequestBody @Validated PaymentRequest dto, @RequestParam Currency targetCurrency) {
        BigDecimal newAmount = service.convertWithCommission(dto, targetCurrency);
        String formattedAmount = formatBigDecimal(newAmount);
        
        String message = String.format(
            "Dear friend! Thank you for converting money! You converted %s %s to %s %s with commission %%",
            formatBigDecimal(dto.amount()),
            dto.currency(),
            formattedAmount,
            targetCurrency,
            exchangeConfig.getCommission()
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
    
    private String formatBigDecimal(BigDecimal amount) {
        return DECIMAL_FORMAT.format(amount);
    }
    
    private int getVerificationCode() {
        return new Random().nextInt(1000, 10000);
    }
}
