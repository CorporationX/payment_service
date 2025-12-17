package faang.school.paymentservice.controller;

import faang.school.paymentservice.dto.TransferDto;
import faang.school.paymentservice.dto.PaymentRequest;
import java.text.DecimalFormat;
import java.util.Random;
import java.util.UUID;

import faang.school.paymentservice.dto.PaymentResponse;
import faang.school.paymentservice.dto.PaymentStatus;
import faang.school.paymentservice.dto.AuthorizationDto;
import faang.school.paymentservice.service.payment.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

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

    @PostMapping("/authorization")
    public ResponseEntity<UUID> processAuthorization(@RequestBody @Valid AuthorizationDto authorizationDto) {
        UUID operationId = paymentService.processAuthorization(authorizationDto);
        return ResponseEntity.ok().body(operationId);
    }

    @PatchMapping("/clearing/{operationId}")
    public void clearingOperation(@PathVariable UUID operationId) {
        paymentService.clearingOperation(operationId);
    }

    @PatchMapping("/cancel/{operationId}")
    public void cancelOperation(@PathVariable UUID operationId) {
        paymentService.cancelOperation(operationId);
    }

    @GetMapping("/{operationId}/{senderAccountId}")
    public ResponseEntity<TransferDto> getBankOperation(@PathVariable UUID operationId, @PathVariable UUID senderAccountId) {
        TransferDto transferDto = paymentService.getTransfer(operationId, senderAccountId);
        return ResponseEntity.ok().body(transferDto);
    }
}
