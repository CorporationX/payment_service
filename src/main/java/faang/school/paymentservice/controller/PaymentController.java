package faang.school.paymentservice.controller;

import faang.school.paymentservice.model.dto.PaymentDto;
import faang.school.paymentservice.model.dto.PaymentRequest;
import faang.school.paymentservice.model.dto.PaymentResponse;
import faang.school.paymentservice.model.enums.Currency;
import faang.school.paymentservice.model.enums.PaymentStatus;
import faang.school.paymentservice.service.PaymentService;
import faang.school.paymentservice.service.impl.CurrencyConverter;
import faang.school.paymentservice.validator.ValidatorPaymentController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Random;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/payment")
public class PaymentController {
    private final CurrencyConverter currencyConverter;
    private final Currency currencyOnOurAccount = Currency.RUB;
    private final ValidatorPaymentController validator;
    private final PaymentService paymentService;

    @Operation(description = "Service for payments")
    @PostMapping("/payment")
    public ResponseEntity<PaymentResponse> sendPayment(@RequestBody @Validated PaymentRequest dto) {
        validator.checkCurrency(dto.currency());
        BigDecimal finalAmount = currencyConverter.getLatestExchangeRates(dto, currencyOnOurAccount);
        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        String formattedSum = decimalFormat.format(finalAmount);
        int verificationCode = new Random().nextInt(1000, 10000);
        String message = String.format("Dear friend! Thank you for your purchase! " +
                        "Your payment on %s %s was accepted.",
                formattedSum, currencyOnOurAccount);
        return ResponseEntity.ok(new PaymentResponse(PaymentStatus.COMPLETED, verificationCode,
                dto.paymentNumber(), finalAmount, currencyOnOurAccount, message)
        );
    }

    @Operation(description = "Create payment")
    @PostMapping()
    @Parameter(name = "x-user-id", in = ParameterIn.HEADER, required = true,
            description = "ID of the user making the request", schema = @Schema(type = "string"))
    public Long createPayment(@RequestBody @Validated(PaymentDto.Create.class) PaymentDto paymentDto) {
        return paymentService.createPayment(paymentDto);
    }

    @Operation(description = "Cancel payment")
    @PutMapping("/{pendingOperationId}")
    @Parameter(name = "x-user-id", in = ParameterIn.HEADER, required = true,
            description = "ID of the user making the request", schema = @Schema(type = "string"))
    public void cancelPayment(@PathVariable Long pendingOperationId) {
        paymentService.cancelPayment(pendingOperationId);
    }

    @Operation(description = "Get payment status")
    @GetMapping("/{pendingOperationId}")
    @Parameter(name = "x-user-id", in = ParameterIn.HEADER, required = true,
            description = "ID of the user making the request", schema = @Schema(type = "string"))
    public PaymentDto getPayment(@PathVariable Long pendingOperationId) {
        return paymentService.getPayment(pendingOperationId);
    }
}
