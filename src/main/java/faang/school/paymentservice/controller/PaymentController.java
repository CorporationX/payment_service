package faang.school.paymentservice.controller;

import faang.school.paymentservice.config.context.UserContext;
import faang.school.paymentservice.dto.*;

import java.text.DecimalFormat;
import java.util.Random;

import faang.school.paymentservice.model.BalanceAudit;
import faang.school.paymentservice.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/payment")
@Validated
public class PaymentController {
    private final PaymentService paymentService;
    private final UserContext userContext;

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

    @PostMapping("/new-account/{accountNumber}")
    public AccountDto createAccount(@PathVariable Long accountNumber){
        return paymentService.createAccount(accountNumber);
    }

    @GetMapping("/balance/{balanceId}")
    public BalanceDto getBalance(@PathVariable Long balanceId){
        return paymentService.getBalance(balanceId);
    }

    @PutMapping("/balance/{balanceId}")
    public BalanceDto updateBalance(@Valid UpdateBalanceDto updateBalanceDto){
        return paymentService.updateBalance(updateBalanceDto);
    }

    @PostMapping("/create-request")
    public ResponseEntity<BalanceAudit> createRequestForPayment(@RequestBody @Validated CreatePaymentRequest dto){
        return ResponseEntity.ok(paymentService.createRequestForPayment(dto, userContext.getUserId()));
    }

    @PutMapping("cancel-request/{balanceAuditId}")
    public ResponseEntity<BalanceAudit> cancelRequestForPayment(@PathVariable Long balanceAuditId){
        return ResponseEntity.ok(paymentService.cancelRequestForPayment(balanceAuditId, userContext.getUserId()));
    }

    @PutMapping("force-request/{balanceAuditId}")
    public ResponseEntity<BalanceAudit> forceRequestForPayment(@PathVariable Long balanceAuditId){
        return ResponseEntity.ok(paymentService.forceRequestForPayment(balanceAuditId, userContext.getUserId()));
    }
}
