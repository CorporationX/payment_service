package faang.school.paymentservice.controller;

import faang.school.paymentservice.config.CurrencyExchangeConfig;
import faang.school.paymentservice.dto.PaymentRequestDto;
import faang.school.paymentservice.dto.PaymentResponseDto;
import faang.school.paymentservice.model.Currency;
import faang.school.paymentservice.model.PaymentRequest;
import faang.school.paymentservice.model.PaymentResponse;
import faang.school.paymentservice.model.PaymentStatus;
import faang.school.paymentservice.response.CurrencyExchangeResponse;
import faang.school.paymentservice.service.CurrencyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Random;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PaymentController {
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("0.00");
    private static final String CONVERTING_MONEY_MESSAGE = "Dear friend! Thank you for converting money! You converted %s %s to %s %s with commission %f%%";

    private final CurrencyService currencyService;
    private final CurrencyExchangeConfig config;

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

    @GetMapping("/currency")
    public CurrencyExchangeResponse getCurrency() {
        return currencyService.getCurrentCurrencyExchangeRate();
    }

    @PostMapping("exchange")
    public ResponseEntity<PaymentResponseDto> exchangeCurrency(@RequestBody @Validated PaymentRequestDto dto, @RequestParam Currency targetCurrency) {
        BigDecimal newAmount = currencyService.convertWithCommission(dto, targetCurrency);

        String message = String.format(
                CONVERTING_MONEY_MESSAGE,
                DECIMAL_FORMAT.format(dto.getAmount()),
                dto.getCurrency(),
                DECIMAL_FORMAT.format(newAmount),
                targetCurrency,
                config.getCommission()
        );

        return ResponseEntity.ok(PaymentResponseDto.builder()
                .status(OperationStatus.CONFIRMED)
                .senderAccountNumber(dto.getSenderAccountNumber())
                .amount(newAmount)
                .currency(targetCurrency)
                .message(message)
                .build()
        );
    }
}
