package faang.school.paymentservice.controller;

import faang.school.paymentservice.client.OpenExchangeClient;
import faang.school.paymentservice.dto.Currency;
import faang.school.paymentservice.dto.PaymentRequest;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Random;
import faang.school.paymentservice.dto.PaymentResponse;
import faang.school.paymentservice.dto.PaymentStatus;
import faang.school.paymentservice.dto.RatesResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static faang.school.paymentservice.dto.Currency.USD;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class PaymentController {
    private final OpenExchangeClient openExchangeClient;
    @Value("${commission}")
    private String COMMISSION;
    @Value("${default-currency}")
    private String DEFAULT_CURRENCY;

    @PostMapping("/payment")
    public ResponseEntity<PaymentResponse> sendPayment(@RequestBody @Validated PaymentRequest dto) {
        BigDecimal convertedAmount = convertAmount(dto);
        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        String formattedSum = decimalFormat.format(convertedAmount);
        int verificationCode = new Random().nextInt(1000, 10000);
        String message = String.format("Dear friend! Thank you for your purchase! " +
                        "Your payment on %s %s was accepted.",
                formattedSum, DEFAULT_CURRENCY);

        return ResponseEntity.ok(new PaymentResponse(
                PaymentStatus.SUCCESS,
                verificationCode,
                dto.paymentNumber(),
                dto.amount(),
                dto.currency(),
                message)
        );
    }

    private BigDecimal convertAmount(PaymentRequest dto) {
        Currency requestCurrency = dto.currency();
        BigDecimal conversionFactor = BigDecimal.ONE;
        if (requestCurrency != USD) {
            RatesResponse ratesResponse = openExchangeClient.getLatest(DEFAULT_CURRENCY, requestCurrency.name());
            if (!ratesResponse.rates().containsKey(requestCurrency.name())) {
                log.error("Currency {} not supported", requestCurrency.name());
                throw new HttpMessageNotReadableException("Currency not supported");
            }
            conversionFactor = ratesResponse.rates().get(requestCurrency.name());
        }
        return dto.amount().divide(conversionFactor, 7, RoundingMode.HALF_UP)
                .multiply(new BigDecimal(COMMISSION));
    }
}
