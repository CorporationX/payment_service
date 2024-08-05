package faang.school.paymentservice.controller;

import faang.school.paymentservice.config.currency.CurrencyExchangeConfig;
import faang.school.paymentservice.dto.Currency;
import faang.school.paymentservice.dto.PaymentRequest;

import java.text.DecimalFormat;
import java.util.Random;
import java.math.BigDecimal;

import faang.school.paymentservice.dto.PaymentResponse;
import faang.school.paymentservice.dto.PaymentStatus;
import faang.school.paymentservice.dto.exchange.CurrencyExchangeResponse;
import faang.school.paymentservice.service.CurrencyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PaymentController {
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("0.00");
    private static final String CONVERTING_MONEY_MESSAGE = "Dear friend! Thank you for converting money! You converted %s %s to %s %s with commission %f%%";
    private static final String PAYMENT_MESSAGE = "Dear friend! Thank you for your purchase!. Your payment on %s %s was accepted.";

    private final CurrencyService currencyService;
    private final CurrencyExchangeConfig exchangeConfig;

    @PostMapping("/payment")
    public ResponseEntity<PaymentResponse> sendPayment(@RequestBody @Validated PaymentRequest dto) {
        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        String formattedSum = decimalFormat.format(dto.amount());
        int verificationCode = new Random().nextInt(1000, 10000);
        String message = String.format("Dear friend! Thank you for your purchase! " +
                        "Your payment on %s %s was accepted.",
                formattedSum, dto.currency().name());

        return ResponseEntity.ok(new PaymentResponse(
                PaymentStatus.SUCCESS,
                verificationCode,
                dto.paymentNumber(),
                dto.amount(),
                dto.currency(),
                message)
        );
    }

    /**
     * Получение текущего соотношения валют к доллару из внешнего источника
     *
     * @return
     */
    @GetMapping("/currency")
    public CurrencyExchangeResponse getCurrentCurrencyExchangeRate() {
        return currencyService.getCurrentCurrencyExchangeRate();
    }

    /**
     * Конвертация одной валюты в другую
     *
     * @param dto            Объект для конвертации
     * @param targetCurrency целевая валюта
     * @return Объект результата конвертации
     */
    @PostMapping("/exchange")
    public ResponseEntity<PaymentResponse> exchangeCurrency(@RequestBody @Validated PaymentRequest dto,
                                                            @RequestParam Currency targetCurrency) {
        BigDecimal newAmount = currencyService.convertWithCommission(dto, targetCurrency);

        String message = String.format(
                CONVERTING_MONEY_MESSAGE,
                DECIMAL_FORMAT.format(dto.amount()),
                dto.currency(),
                DECIMAL_FORMAT.format(newAmount),
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

    private int getVerificationCode() {
        return new Random().nextInt(1000, 10000);
    }
}