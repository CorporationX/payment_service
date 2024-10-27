package faang.school.paymentservice.scheduler;

import faang.school.paymentservice.dto.event.PaymentClearEvent;
import faang.school.paymentservice.listener.PaymentCancelEventListener;
import faang.school.paymentservice.model.OperationState;
import faang.school.paymentservice.model.PendingOperation;
import faang.school.paymentservice.publisher.PaymentClearEventPublisher;
import faang.school.paymentservice.repository.PendingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentApproveScheduler {
    private final PendingRepository pendingRepository;
    private final PaymentClearEventPublisher paymentClearEventPublisher;

    @Scheduled(cron = "*/10 * * * * *")
    public void publishClearEvent() {
        List<PendingOperation> approvedOperations = new ArrayList<>();
        pendingRepository.findAll().forEach(pendingOperation -> {
            if(pendingOperation.getState() == OperationState.APPROVED) {
                approvedOperations.add(pendingOperation);
            }
        });
        approvedOperations.forEach(pendingOperation -> {
            paymentClearEventPublisher.publish(new PaymentClearEvent().builder()
                    .userId(pendingOperation.getAccountId())
                    .amount(pendingOperation.getAmount())
                    .operationKey(pendingOperation.getOperationKey())
                    .build());
            log.info("ClearEvent with {} user id successfully publish", pendingOperation.getAccountId());
            pendingOperation.setState(OperationState.CLEARED);
            pendingRepository.save(pendingOperation);
        });
    }
}
