package faang.school.paymentservice.service.operation;

import faang.school.paymentservice.dto.event.PaymentRequestEvent;
import faang.school.paymentservice.model.OperationState;
import faang.school.paymentservice.model.PendingOperation;
import faang.school.paymentservice.repository.PendingRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class OperationServiceImpl implements OperationService {
    private final PendingRepository pendingRepository;

    @Transactional
    public void savePendingOperation(PaymentRequestEvent event) {
        PendingOperation pendingOperation = new PendingOperation().builder()
                .accountId(event.getUserId())
                .state(OperationState.PENDING)
                .amount(event.getAmount())
                .createdAt(LocalDateTime.now())
                .operationKey(event.getOperationKey())
                .build();
        pendingRepository.save(pendingOperation);
    }



    @Override
    public void updatePendingOperation(PaymentRequestEvent event) {
        var pending = pendingRepository.findById(event.getUserId())
                .orElseThrow(() -> new EntityNotFoundException(String.format("Entity not found with %s user id", event.getUserId())));

    }
}
