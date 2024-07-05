package faang.school.paymentservice.controller;

import faang.school.paymentservice.config.currency.CurrencyExchangeConfig;
import faang.school.paymentservice.dto.Currency;
import faang.school.paymentservice.dto.PaymentRequestDto;
import faang.school.paymentservice.dto.PaymentResponse;
import faang.school.paymentservice.dto.PaymentStatus;
import faang.school.paymentservice.dto.exchange.CurrencyExchangeResponse;
import faang.school.paymentservice.service.CurrencyConverterService;
import faang.school.paymentservice.service.PaymentService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Random;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PaymentController {
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("0.00");
    private static final String CONVERTING_MONEY_MESSAGE = "Dear friend! Thank you for converting money! You converted %s %s to %s %s with commission %f%%";
    private static final String PAYMENT_MESSAGE = "Dear friend! Thank you for your purchase!. Your payment on %s %s was accepted.";

    private final CurrencyConverterService service;
    private final CurrencyExchangeConfig exchangeConfig;

    /**
     * Получение текущего соотношения валют к доллару из внешнего источника
     *
     * @return
     */
    @GetMapping("currency")
    public CurrencyExchangeResponse getCurrentCurrencyExchangeRate() {
        return service.getCurrentCurrencyExchangeRate();
    }

    private final PaymentService paymentService;

    @PostMapping("/payment")
    public PaymentResponse sendPayment(@RequestBody @Validated PaymentRequestDto dto) {
        return paymentService.sendPayment(dto);
    }

    @PatchMapping("/payment/{paymentId}/cancel")
    public PaymentResponse cancelPayment(@PathVariable @NotNull Long paymentId) {
        return paymentService.cancelPayment(paymentId);
    }

    @PatchMapping("/payment/{paymentId}/confirm")
    public PaymentResponse confirmPayment(@PathVariable @NotNull Long paymentId, @RequestParam(required = false) BigDecimal newAmount) {
        return paymentService.confirmPayment(paymentId, newAmount);
    }

    /**
     * Конвертация одной валюты в другую
     *
     * @param dto            Объект для конвертации
     * @param targetCurrency целевая валюта
     * @return Объект результата конвертации
     */
    @PostMapping("exchange")
    public ResponseEntity<PaymentResponse> exchangeCurrency(@RequestBody @Validated PaymentRequestDto dto, @RequestParam Currency targetCurrency) {
        BigDecimal newAmount = service.convertWithCommission(dto, targetCurrency);

        String message = String.format(
                CONVERTING_MONEY_MESSAGE,
                DECIMAL_FORMAT.format(dto.getAmount()),
                dto.getCurrency(),
                DECIMAL_FORMAT.format(newAmount),
                targetCurrency,
                exchangeConfig.getCommission()
        );

        return ResponseEntity.ok(PaymentResponse.builder()
                .status(PaymentStatus.SUCCESS)
                .debitAccountId(dto.getDebitAccountId())
                .amount(newAmount)
                .currency(targetCurrency)
                .message(message)
                .build()
        );
    }

    private int getVerificationCode() {
        return new Random().nextInt(1000, 10000);
    }
}
