package faang.school.paymentservice.controller;

import faang.school.paymentservice.dto.Currency;
import faang.school.paymentservice.dto.PaymentRequest;
import faang.school.paymentservice.dto.PaymentResponse;
import faang.school.paymentservice.dto.PaymentStatus;
import faang.school.paymentservice.dto.convert.ConvertDto;
import faang.school.paymentservice.exception.PaymentException;
import faang.school.paymentservice.service.converter.CurrencyConverterService;
import faang.school.paymentservice.service.rates.CurrencyFetchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Map;
import java.util.Random;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/payments")
public class PaymentController {

    private final CurrencyConverterService currencyConverterService;
    private final CurrencyFetchService currencyFetchService;

    @Value("${default.target.currency}")
    private Currency targetCurrency;

    @PostMapping("/payment")
    public ResponseEntity<PaymentResponse> sendPayment(@RequestBody @Validated PaymentRequest dto) {
        try {
            BigDecimal convertedAmount = currencyConverterService.convert(
                    new ConvertDto(dto.amount(), dto.currency(), targetCurrency));

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
            throw new PaymentException("Error processing payment");
        }
    }

    @GetMapping("/convert")
    public BigDecimal convert(@RequestBody ConvertDto convertDto) {
        return currencyConverterService.convert(convertDto);
    }

    @GetMapping("/rates")
    public Map<String, Double> getRates() {
        return currencyFetchService.fetch();
    }
}