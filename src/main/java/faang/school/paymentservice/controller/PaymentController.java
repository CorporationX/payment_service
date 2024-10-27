package faang.school.paymentservice.controller;

import faang.school.paymentservice.dto.PaymentRequestDto;
import faang.school.paymentservice.dto.event.PaymentCancelEvent;
import faang.school.paymentservice.dto.event.PaymentRequestEvent;
import faang.school.paymentservice.dto.PaymentResponseDto;
import faang.school.paymentservice.dto.response.CurrencyExchangeResponse;
import faang.school.paymentservice.model.Currency;
import faang.school.paymentservice.model.PaymentRequest;
import faang.school.paymentservice.model.PaymentResponse;
import faang.school.paymentservice.model.PaymentStatus;
import faang.school.paymentservice.service.currency.CurrencyService;
import faang.school.paymentservice.service.payment.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Random;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class PaymentController {
    private final CurrencyService currencyService;
    private final PaymentService paymentService;

    @PostMapping("/payment")
    public ResponseEntity<PaymentResponse> sendPayment(@RequestBody PaymentRequest dto) {
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

    @PostMapping("/payment/request")
    public void requestPayment(@RequestBody PaymentRequestEvent event) {
        paymentService.requestPayment(event);
    }

    @GetMapping("/currency")
    public CurrencyExchangeResponse getCurrency() {
        return currencyService.getCurrentCurrencyExchangeRate();
    }

    @PostMapping("/exchange")
    public ResponseEntity<PaymentResponseDto> exchangeCurrency(@RequestBody PaymentRequestDto dto, @RequestParam Currency targetCurrency) {
        BigDecimal newAmount = currencyService.convertWithCommission(dto, targetCurrency);
        String message = currencyService.createMessage(dto, newAmount, targetCurrency);
        return ResponseEntity.ok(PaymentResponseDto.builder()
                .amount(newAmount)
                .currency(targetCurrency)
                .message(message)
                .build()
        );
    }
}
