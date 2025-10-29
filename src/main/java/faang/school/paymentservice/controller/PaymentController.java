package faang.school.paymentservice.controller;

import faang.school.paymentservice.client.ExchangeRatesClient;
import faang.school.paymentservice.dto.Currency;
import faang.school.paymentservice.dto.ExchangeRatesDto;
import faang.school.paymentservice.dto.PaymentRequest;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Random;
import faang.school.paymentservice.dto.PaymentResponse;
import faang.school.paymentservice.dto.PaymentStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PaymentController {
    private final ExchangeRatesClient exchangeRatesClient;

    @PostMapping("/payment")
    public ResponseEntity<PaymentResponse> sendPayment(@RequestBody @Validated PaymentRequest dto) {
        Currency requiredCurrency = Currency.USD;
        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        String formattedSum = decimalFormat.format(dto.amount());
        int verificationCode = new Random().nextInt(1000, 10000);
        String message = String.format("Dear friend! Thank you for your purchase! " +
                        "Your payment on %s %s was accepted.",
                formattedSum, dto.currency().name());
        double currencyConversionFee = 0.01;
        try {
            ExchangeRatesDto exchangeRatesDto = exchangeRatesClient.getRates(dto.currency().toString());
            double rate = exchangeRatesDto.rates().get(dto.currency().toString());
            double amountInRequiredCurrency = dto.amount().toBigInteger().doubleValue() / rate;
            double amountAfterCurrencyConversion =
                    amountInRequiredCurrency - amountInRequiredCurrency * currencyConversionFee;
            return ResponseEntity.ok(new PaymentResponse(
                    PaymentStatus.SUCCESS,
                    verificationCode,
                    dto.paymentNumber(),
                    new BigDecimal(amountAfterCurrencyConversion),
                    requiredCurrency,
                    message)
            );
        } catch (Exception e) {
            log.error("Попытка оплатить валютой c обозначением {}.", dto.currency());
            throw new RuntimeException("Оплата данной валютой не принимается.");
        }
    }
}
