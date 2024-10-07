package faang.school.paymentservice.controller;

import faang.school.paymentservice.config.currency.CurrencyExchangeParams;
import faang.school.paymentservice.dto.Currency;
import faang.school.paymentservice.dto.PaymentRequest;
import faang.school.paymentservice.dto.PaymentResponse;
import faang.school.paymentservice.dto.PaymentStatus;
import faang.school.paymentservice.service.CurrencyExchangeService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
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
@Validated
public class PaymentController {
    private static final String CONVERTING_MONEY_MESSAGE =
            "Dear friend! Thank you for converting money! " +
                    "You converted %s %s to %s %s with commission %.2f%%";
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("0.00");
    private final CurrencyExchangeService currencyExchangeService;
    private final CurrencyExchangeParams currencyExchangeParams;

    @PostMapping("/exchange")
    public PaymentResponse exchangeCurrency(
            @RequestBody @Validated PaymentRequest dto, @RequestParam Currency toCurrency) {
        int verificationCode = new Random().nextInt(1000, 10000);
        BigDecimal newAmount = currencyExchangeService.convertWithCommission(dto, toCurrency);

        String message = String.format(
                CONVERTING_MONEY_MESSAGE,
                DECIMAL_FORMAT.format(dto.amount()),
                dto.currency(),
                DECIMAL_FORMAT.format(newAmount),
                toCurrency,
                currencyExchangeParams.getCommission()
        );
        return new PaymentResponse(
                PaymentStatus.SUCCESS,
                verificationCode,
                dto.paymentNumber(),
                dto.amount(),
                dto.currency(),
                message);
    }
}