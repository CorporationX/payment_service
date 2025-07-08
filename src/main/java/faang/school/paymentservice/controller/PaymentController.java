package faang.school.paymentservice.controller;

import faang.school.paymentservice.dto.PaymentRequest;

import java.math.BigDecimal;
import java.net.URISyntaxException;

import faang.school.paymentservice.dto.PaymentResponse;
import faang.school.paymentservice.dto.PaymentStatus;
import faang.school.paymentservice.dto.transfer.CancelTransferRequest;
import faang.school.paymentservice.dto.transfer.ForceClearingTransferRequest;
import faang.school.paymentservice.dto.transfer.TransferRequest;
import faang.school.paymentservice.dto.transfer.TransferResponse;
import faang.school.paymentservice.service.payment.PaymentService;
import faang.school.paymentservice.verification.VerificationData;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/payment")
public class PaymentController {

    private final PaymentService paymentService;
    private final VerificationData verification;

    @PostMapping("/transfer")
    public ResponseEntity<TransferResponse> startTransferAuthorization(@RequestBody TransferRequest dto) {
        TransferResponse response = paymentService.startTransferAuthorization(dto);

        return ResponseEntity.ok();
    }

    @DeleteMapping("/transfer/cancellation")
    public ResponseEntity<TransferResponse> cancelTransferAuthorization(@RequestBody CancelTransferRequest dto) {


        return ResponseEntity.ok();
    }

    @PostMapping("/transfer/forced-clearing")
    public ResponseEntity<TransferResponse> forceTransferAuthorization(@RequestBody ForceClearingTransferRequest dto) {


        return ResponseEntity.ok();
    }

    @PostMapping("/convert")
    public ResponseEntity<PaymentResponse> convertCurrency(@RequestBody @Validated PaymentRequest dto){
        BigDecimal converter = paymentService.convertCurrency(dto.amount(), dto.fromCurrency().name(),
                dto.toCurrency().name());

        return ResponseEntity.ok(new PaymentResponse(
                PaymentStatus.SUCCESS,
                verification.verificationCode(),
                dto.paymentNumber(),
                converter,
                dto.fromCurrency(),
                verification.addMessage(dto))
        );
    }
}
