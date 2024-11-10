package faang.school.paymentservice.service.impl;

import faang.school.paymentservice.model.entity.PendingOperation;
import faang.school.paymentservice.model.enums.PaymentStatus;
import faang.school.paymentservice.model.event.PaymentStatusEvent;
import faang.school.paymentservice.repository.PendingOperationRepository1;
import faang.school.paymentservice.service.PaymentStatusService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentStatusServiceImpl implements PaymentStatusService {
    private final PendingOperationRepository1 pendingOperationRepository;

    @Override
    @Transactional
    public void processInProgressStatus(PaymentStatusEvent event) {
        PendingOperation pendingOperation = pendingOperationRepository.findById(event.getIdempotencyToken()).orElseThrow(() ->
                new EntityNotFoundException(String.format("Pending operation with id = %s not found", event.getIdempotencyToken())));
        pendingOperation.setStatus(PaymentStatus.IN_PROGRESS);
        pendingOperation.setStatusDetails(null);
        pendingOperationRepository.save(pendingOperation);
    }

    @Override
    public void processCompletedStatus(PaymentStatusEvent event) {
        //TODO дописать
    }

    @Override
    public void processCancelledStatus(PaymentStatusEvent event) {
        PendingOperation pendingOperation = pendingOperationRepository.findById(event.getIdempotencyToken()).orElseThrow(() ->
                new EntityNotFoundException(String.format("Pending operation with id = %s not found", event.getIdempotencyToken())));
        pendingOperation.setStatus(PaymentStatus.CANCELED);
        pendingOperation.setStatusDetails(null);
        pendingOperationRepository.save(pendingOperation);
    }

    @Override
    public void processFailedStatus(PaymentStatusEvent event) {
        PendingOperation pendingOperation = pendingOperationRepository.findById(event.getIdempotencyToken()).orElseThrow(() ->
                new EntityNotFoundException(String.format("Pending operation with id = %s not found", event.getIdempotencyToken())));
        pendingOperation.setStatus(PaymentStatus.FAILED);
        pendingOperation.setStatusDetails(event.getStatusDetails());
        pendingOperationRepository.save(pendingOperation);
    }
}
