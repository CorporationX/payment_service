package faang.school.paymentservice.controller.payment;

import faang.school.paymentservice.dto.payment.PaymentOperationAuthorizeRequestDto;
import faang.school.paymentservice.dto.payment.PaymentOperationResponseDto;
import faang.school.paymentservice.facade.payment.PaymentOperationFacade;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/payment")
@Slf4j
@RequiredArgsConstructor
public class PaymentOperationController {
    private final PaymentOperationFacade paymentOperationFacade;
    @GetMapping("/{paymentOperationId}")
    public ResponseEntity<PaymentOperationResponseDto> getPaymentOperationById(@PathVariable UUID paymentOperationId) {
        log.info("PaymentOperation controller accepted request get payment operation by id {}", paymentOperationId);

        PaymentOperationResponseDto response = paymentOperationFacade.getPaymentOperationById(paymentOperationId);
        log.info("PaymentOperation controller return response get payment operation by id {}", response);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/authorize")
    public ResponseEntity<PaymentOperationResponseDto> authorizePayment
            (@RequestBody @Valid PaymentOperationAuthorizeRequestDto requestDto) {
        log.info("PaymentOperation controller accepted request authorize payment {}", requestDto);

        PaymentOperationResponseDto response = paymentOperationFacade.authorizePayment(requestDto);
        log.info("PaymentOperation controller return response authorize payment {}", response);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/clear")
    public ResponseEntity<PaymentOperationResponseDto> clearPayment(@PathVariable UUID paymentOperationId) {
        log.info("PaymentOperation controller accepted request clear payment {}", paymentOperationId);

        PaymentOperationResponseDto response = paymentOperationFacade.clearPayment(paymentOperationId);
        log.info("PaymentOperation controller return response clear payment {}", response);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/cancel")
    public ResponseEntity<PaymentOperationResponseDto> cancelPayment(@PathVariable UUID paymentOperationId) {
        log.info("PaymentOperation controller accepted request cancel payment {}", paymentOperationId);

        PaymentOperationResponseDto response = paymentOperationFacade.cancelPayment(paymentOperationId);
        log.info("PaymentOperation controller return response cancel payment {}", response);

        return ResponseEntity.ok(response);
    }
}
