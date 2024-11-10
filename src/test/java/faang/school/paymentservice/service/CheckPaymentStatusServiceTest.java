package faang.school.paymentservice.service;

import faang.school.paymentservice.dto.PaymentStatus;
import faang.school.paymentservice.dto.PaymentStatusResponseDto;
import faang.school.paymentservice.exception.ErrorOperationException;
import faang.school.paymentservice.model.OperationStatus;
import faang.school.paymentservice.model.PendingOperation;
import faang.school.paymentservice.repository.PendingOperationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CheckPaymentStatusServiceTest {
    @Mock
    private PendingOperationRepository pendingOperationRepository;

    @InjectMocks
    private CheckPaymentStatusService checkPaymentStatusService;

    private PendingOperation pendingOperation;
    private UUID operationId;

    @BeforeEach
    public void setUp() {
        operationId = UUID.randomUUID();
        pendingOperation = PendingOperation.builder()
                .id(operationId)
                .status(OperationStatus.CLEARING)
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    public void testCheckPaymentStatus_FailedStatus_Clearing() {
        PaymentStatusResponseDto event = new PaymentStatusResponseDto(operationId, PaymentStatus.FAILED);

        when(pendingOperationRepository.findById(operationId)).thenReturn(Optional.of(pendingOperation));

        assertThrows(ErrorOperationException.class, () ->
                checkPaymentStatusService.checkPaymentStatus(event));

        verify(pendingOperationRepository, times(1)).save(pendingOperation);
        assertEquals(OperationStatus.ERROR, pendingOperation.getStatus());
    }

    @Test
    public void testCheckPaymentStatus_SuccessStatus_Clearing() {
        pendingOperation.setStatus(OperationStatus.CLEARING);
        PaymentStatusResponseDto event = new PaymentStatusResponseDto(operationId, PaymentStatus.SUCCESS);

        when(pendingOperationRepository.findById(operationId)).thenReturn(Optional.of(pendingOperation));

        checkPaymentStatusService.checkPaymentStatus(event);

        verify(pendingOperationRepository, times(1)).save(pendingOperation);
        assertEquals(OperationStatus.FINISHED_CLEARING, pendingOperation.getStatus());
    }

    @Test
    public void testCheckPaymentStatus_SuccessStatus_Cancellation() {
        pendingOperation.setStatus(OperationStatus.CANCELLATION);
        PaymentStatusResponseDto event = new PaymentStatusResponseDto(operationId, PaymentStatus.SUCCESS);

        when(pendingOperationRepository.findById(operationId)).thenReturn(Optional.of(pendingOperation));

        checkPaymentStatusService.checkPaymentStatus(event);

        verify(pendingOperationRepository, times(1)).save(pendingOperation);
        assertEquals(OperationStatus.FINISHED_CANCELLATION, pendingOperation.getStatus());
    }

    @Test
    public void testCheckPaymentStatus_SuccessStatus_Error() {
        pendingOperation.setStatus(OperationStatus.ERROR);
        PaymentStatusResponseDto event = new PaymentStatusResponseDto(operationId, PaymentStatus.SUCCESS);

        when(pendingOperationRepository.findById(operationId)).thenReturn(Optional.of(pendingOperation));

        checkPaymentStatusService.checkPaymentStatus(event);

        verify(pendingOperationRepository, times(1)).save(pendingOperation);
        assertEquals(OperationStatus.FINISHED_ERROR, pendingOperation.getStatus());
    }

    @Test
    public void testCheckPaymentStatus_OperationNotFound() {
        PaymentStatusResponseDto event = new PaymentStatusResponseDto(operationId, PaymentStatus.SUCCESS);

        when(pendingOperationRepository.findById(operationId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () ->
                checkPaymentStatusService.checkPaymentStatus(event));
    }
}