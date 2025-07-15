package faang.school.paymentservice.controller;

import faang.school.paymentservice.dto.PaymentRequest;

import java.math.BigDecimal;

import faang.school.paymentservice.dto.PaymentResponse;
import faang.school.paymentservice.dto.TransferStage;
import faang.school.paymentservice.dto.transfer.CancelTransferRequest;
import faang.school.paymentservice.dto.transfer.ForceClearingTransferRequest;
import faang.school.paymentservice.dto.transfer.TransferRequest;
import faang.school.paymentservice.dto.transfer.TransferResponse;
import faang.school.paymentservice.facade.TransferFacade;
import faang.school.paymentservice.service.payment.PaymentService;
import faang.school.paymentservice.verification.VerificationData;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/payment")
public class PaymentController {

    private final TransferFacade transferFacade;
    private final PaymentService paymentService;
    private final VerificationData verification;

    @PostMapping("/transfer")
    public ResponseEntity<TransferResponse> startTransferAuthorization(@RequestBody @Validated TransferRequest dto) {
        TransferResponse response = transferFacade.startTransferAuthorization(dto);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/transfer/cancellation")
    public ResponseEntity<TransferResponse> cancelTransfer(@RequestBody @Validated CancelTransferRequest dto) {
        TransferResponse response = transferFacade.cancelTransfer(dto);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/transfer/forced-clearing")
    public ResponseEntity<TransferResponse> forceTransferClearing(@RequestBody @Validated ForceClearingTransferRequest dto) {
        TransferResponse response = transferFacade.forceTransferClearing(dto);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/convert")
    public ResponseEntity<PaymentResponse> convertCurrency(@RequestBody @Validated PaymentRequest dto){
        BigDecimal converter = paymentService.convertCurrency(dto.amount(), dto.fromCurrency().name(),
                dto.toCurrency().name());

        return ResponseEntity.ok(new PaymentResponse(
                TransferStage.SUCCESS,
                verification.verificationCode(),
                dto.paymentNumber(),
                converter,
                dto.fromCurrency(),
                verification.addMessage(dto))
        );
    }
}
