package faang.school.paymentservice.service.operation;

import faang.school.paymentservice.dto.event.PaymentRequestEvent;
import faang.school.paymentservice.model.PendingOperation;
import faang.school.paymentservice.repository.PendingRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OperationServiceImplTest {

    @InjectMocks
    private OperationServiceImpl operationService;

    @Mock
    private PendingRepository pendingRepository;

    @Test
    void savePendingOperation() {
        PendingOperation pendingOperation = new PendingOperation();
        PaymentRequestEvent paymentRequestEvent = new PaymentRequestEvent();
        when(pendingRepository.save(any(PendingOperation.class))).thenReturn(pendingOperation);
        operationService.savePendingOperation(paymentRequestEvent);
        Mockito.verify(pendingRepository).save(any(PendingOperation.class));
    }
}