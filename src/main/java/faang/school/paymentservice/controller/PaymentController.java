package faang.school.paymentservice.controller;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Random;

import faang.school.paymentservice.dto.Currency;
import faang.school.paymentservice.dto.PaymentRequestDto;
import faang.school.paymentservice.dto.PaymentResponseDto;
import faang.school.paymentservice.dto.PaymentStatus;
import faang.school.paymentservice.service.currency.CurrencyService;
import faang.school.paymentservice.service.payment.PaymentMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PaymentController {

    @Value("${currency-api.baseCurrency}")
    private Currency baseCurrency;

    private final CurrencyService currencyService;
    private final PaymentMessageService paymentMessageService;

    @PostMapping("/payment")
    public ResponseEntity<PaymentResponseDto> sendPayment(@RequestBody @Validated PaymentRequestDto paymentRequestDto) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setDecimalSeparator('.');
        DecimalFormat decimalFormat = new DecimalFormat("0.00", symbols);
        String formattedSum = decimalFormat.format(paymentRequestDto.getAmount());
        int verificationCode = new Random().nextInt(1000, 10000);
        BigDecimal amountInBaseCurrency = currencyService.convertCurrency(paymentRequestDto, formattedSum);
        String formattedSumInBaseCurrency = decimalFormat.format(amountInBaseCurrency);

        String message = paymentMessageService.generateMessageAfterPayment(formattedSum, paymentRequestDto.getCurrency(),formattedSumInBaseCurrency, baseCurrency);

        return ResponseEntity.ok(new PaymentResponseDto(
                PaymentStatus.SUCCESS,
                verificationCode,
                paymentRequestDto.getPaymentNumber(),
                paymentRequestDto.getAmount(),
                paymentRequestDto.getCurrency(),
                message)
        );
    }
}
