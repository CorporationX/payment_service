package faang.school.paymentservice.service.operation;

import faang.school.paymentservice.dto.event.PaymentRequestEvent;
import faang.school.paymentservice.model.OperationState;
import faang.school.paymentservice.model.PendingOperation;
import faang.school.paymentservice.repository.PendingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class OperationServiceImpl implements OperationService {
    private final PendingRepository pendingRepository;


    @Transactional
    @Override
    public void savePendingOperation(PaymentRequestEvent event) {
        PendingOperation pendingOperation = new PendingOperation().builder()
                .accountId(event.getUserId())
                .state(OperationState.PENDING)
                .amount(event.getAmount())
                .createdAt(LocalDateTime.now())
                .operationKey(event.getOperationKey())
                .build();
        pendingRepository.save(pendingOperation);
        log.info("Save operation pending operation: {}", pendingOperation);
    }
}
