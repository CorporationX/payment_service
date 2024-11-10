package faang.school.paymentservice.controller;

import faang.school.paymentservice.dto.payment.PaymentUpdateDto;
import faang.school.paymentservice.dto.payment.PaymentCreateDto;
import faang.school.paymentservice.dto.payment.PaymentDto;
import faang.school.paymentservice.service.exchangerate.CurrencyService;
import faang.school.paymentservice.service.payment.PaymentOperationService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/payments")
@AllArgsConstructor
public class PaymentController {

    private final CurrencyService currencyService;
    private final PaymentOperationService paymentOperationService;

    @Value("${currency.baseCurrencyPayment}")
    private Currency baseCurrencyPayment;

    @PostMapping
    public PaymentDto sendPayment(@RequestBody @Valid PaymentCreateDto dto) {
        DecimalFormat decimalFormat = new DecimalFormat("0.00");

        BigDecimal amount = dto.amount();
        if (!dto.currency().equals(baseCurrencyPayment)) {
            amount = currencyService.convertCurrency(dto.amount(), dto.currency(), baseCurrencyPayment);

            dto.setAmount(amount);
            dto.setCurrency(baseCurrencyPayment.name());
        }

        return paymentOperationService.sendPayment(dto);
    }

    @PatchMapping("/{paymentId}")
    public PaymentDto updatePayment(@PathVariable @NonNull UUID paymentId,
                                    @RequestBody @Valid PaymentUpdateDto paymentUpdateRequestDto) {
        return switch (paymentUpdateRequestDto.getAction()) {
            case CONFIRM -> paymentOperationService.confirmPayment(paymentId);
            case CANCEL -> paymentOperationService.cancelPayment(paymentId);
        };
    }
}
