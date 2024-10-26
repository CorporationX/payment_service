package faang.school.paymentservice.controller;

import faang.school.paymentservice.dto.request.RequestDto;
import faang.school.paymentservice.service.payment.PaymentRequestService;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/payment")
public class PaymentRequestController {
    private final PaymentRequestService requestService;

    @PostMapping("/authorisation")
    public RequestDto authorizePayment(@RequestBody RequestDto requestDto) {
        return requestService.authorizePayment(requestDto);
    }

    @PutMapping("/cancel/{requestId}")
    public RequestDto cancelPayment(@Positive @PathVariable("requestId") long requestId) {
        return requestService.cancelPayment(requestId);
    }

    @PutMapping("/forciblyConfirm/{requestId}")
    public RequestDto forciblyConfirmPayment(@Positive @PathVariable("requestId") long requestId) {
        return requestService.forciblyConfirmPayment(requestId);
    }

    @GetMapping("/{requestId}")
    public RequestDto getRequest(@Positive @PathVariable("requestId") long requestId) {
        return requestService.getRequest(requestId);
    }

    @Scheduled(cron = "${payment-request.cron}")
    public void pushPaymentConfirmation() {
        requestService.pushPaymentConfirmation();
    }
}
