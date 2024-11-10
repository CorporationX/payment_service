package faang.school.paymentservice.controller;

import faang.school.paymentservice.model.dto.PaymentDto;
import faang.school.paymentservice.model.dto.PendingOperationDto;
import faang.school.paymentservice.model.enums.Currency;
import faang.school.paymentservice.service.PendingOperationService;
import faang.school.paymentservice.service.impl.CurrencyConverter;
import faang.school.paymentservice.validator.ValidatorPaymentController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/payment")
public class PendingOperationController {
    private final CurrencyConverter currencyConverter;
    private final Currency currencyOnOurAccount = Currency.RUB;
    private final ValidatorPaymentController validator;
    private final PendingOperationService pendingOperationService;

    @Operation(description = "Create payment")
    @PostMapping()
    @Parameter(name = "x-user-id", in = ParameterIn.HEADER, required = true,
            description = "ID of the user making the request", schema = @Schema(type = "string"))
    public UUID createPayment(@RequestBody @Validated PendingOperationDto pendingOperationDto) {
        return pendingOperationService.createPayment(pendingOperationDto);
    }

    @Operation(description = "Cancel payment")
    @PutMapping("/{pendingOperationId}")
    @Parameter(name = "x-user-id", in = ParameterIn.HEADER, required = true,
            description = "ID of the user making the request", schema = @Schema(type = "string"))
    public void cancelPayment(@PathVariable UUID pendingOperationId) {
        pendingOperationService.cancelPayment(pendingOperationId);
    }

    @Operation(description = "Get payment status")
    @GetMapping("/{pendingOperationId}")
    @Parameter(name = "x-user-id", in = ParameterIn.HEADER, required = true,
            description = "ID of the user making the request", schema = @Schema(type = "string"))
    public PendingOperationDto getPayment(@PathVariable UUID pendingOperationId) {
        return pendingOperationService.getPendingOperation(pendingOperationId);
    }
}
