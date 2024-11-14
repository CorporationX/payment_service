package faang.school.paymentservice.service.payment;

import faang.school.paymentservice.dto.event.PaymentApproveEvent;
import faang.school.paymentservice.dto.event.PaymentCancelEvent;
import faang.school.paymentservice.dto.event.PaymentRequestEvent;
import faang.school.paymentservice.model.OperationState;
import faang.school.paymentservice.model.PendingOperation;
import faang.school.paymentservice.publisher.PaymentRequestEventPublisher;
import faang.school.paymentservice.repository.PendingRepository;
import faang.school.paymentservice.service.operation.OperationService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private final PaymentRequestEventPublisher eventPublisher;
    private final OperationService operationService;
    private final PendingRepository pendingRepository;

    @Override
    public void requestPayment(PaymentRequestEvent event) {
        event.setOperationKey(makeOperationKey(event));
        operationService.savePendingOperation(event);
        eventPublisher.publish(event);
    }

    private String makeOperationKey(PaymentRequestEvent event) {
        return LocalDateTime.now() + "_" + event.getUserId() + "_" + event.getAmount();
    }

    @Transactional
    @Override
    public void cancelPayment(PaymentCancelEvent event) {
        PendingOperation operation = pendingRepository.findByOperationKey(event.getOperationKey())
                .orElseThrow(() -> new EntityNotFoundException("Operation not found"));
        operation.setState(OperationState.CANCELED);
        log.info("Operation cancelled: {}", operation.getOperationKey());
    }

    @Transactional
    @Override
    public void approvePayment(PaymentApproveEvent event) {
        PendingOperation operation = pendingRepository.findByOperationKey(event.getOperationKey())
                .orElseThrow(() -> new EntityNotFoundException("Operation not found"));
        operation.setState(OperationState.APPROVED);
        log.info("Operation approved: {}", operation.getOperationKey());
    }
}
