package faang.school.paymentservice.controller;

import faang.school.paymentservice.dto.Currency;
import faang.school.paymentservice.dto.PaymentRequest;
import java.text.DecimalFormat;
import java.util.Random;

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
    private final CurrencyService currencyService;

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
     * @return Строку результата конвертации
     */
    @PostMapping("/exchange")
    public String exchangeCurrency(@RequestBody @Validated PaymentRequest dto,
                                                            @RequestParam Currency targetCurrency) {
        String message = currencyService.convertWithCommission(dto, targetCurrency);

        return message;
    }

    private int getVerificationCode() {
        return new Random().nextInt(1000, 10000);
    }
}