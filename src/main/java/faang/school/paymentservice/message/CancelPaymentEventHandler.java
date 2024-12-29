package faang.school.paymentservice.message;

import faang.school.paymentservice.dto.PaymentStatus;
import faang.school.paymentservice.dto.payment.AuthorizationEvent;
import faang.school.paymentservice.enums.ResponseMessageStatus;
import faang.school.paymentservice.model.Request;
import faang.school.paymentservice.repository.PaymentRepository;
import faang.school.paymentservice.service.PaymentService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CancelPaymentEventHandler {

    private final PaymentRepository paymentRepository;
    private final PaymentService paymentService;

    public void handle(AuthorizationEvent event) {
        Request request = paymentRepository.findRequestByVerificationCode(event.getVerificationCode()).orElseThrow(
                () -> new EntityNotFoundException("Request not found"));

        request.setStatus(PaymentStatus.CANCELLED);


        paymentService.updateRequest(request);
        log.info("Request got status CANCELLED: {}", request);
    }
}
