package faang.school.paymentservice.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import faang.school.paymentservice.dto.PaymentStatus;
import faang.school.paymentservice.exception.InsufficientBalanceException;
import faang.school.paymentservice.exception.ErrorOperationException;
import faang.school.paymentservice.model.AccountBalanceStatus;
import faang.school.paymentservice.model.OperationStatus;
import faang.school.paymentservice.model.PendingOperation;
import faang.school.paymentservice.repository.PendingOperationRepository;
import faang.school.paymentservice.dto.CheckingPaymentStatusAndBalance;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
public class CheckingBalanceServiceTest {

    @Mock
    private PendingOperationRepository pendingOperationRepository;

    @InjectMocks
    private CheckingBalanceService checkingBalanceService;

    private PendingOperation pendingOperation;
    private UUID operationId;

    @BeforeEach
    public void setUp() {
        operationId = UUID.randomUUID();
        pendingOperation = PendingOperation.builder()
                .id(operationId)
                .sourceAccountId(UUID.randomUUID())
                .targetAccountId(UUID.randomUUID())
                .amount(new BigDecimal("100.00"))
                .status(OperationStatus.PENDING)
                .accountBalanceStatus(AccountBalanceStatus.BALANCE_NOT_VERIFIED)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    public void testCheckBalance_InsufficientFunds_FailedStatus() {
        CheckingPaymentStatusAndBalance event = new CheckingPaymentStatusAndBalance(
                operationId, AccountBalanceStatus.INSUFFICIENT_FUNDS, PaymentStatus.FAILED);

        when(pendingOperationRepository.findById(operationId)).thenReturn(Optional.of(pendingOperation));

        assertThrows(InsufficientBalanceException.class, () ->
                checkingBalanceService.checkBalance(event, AccountBalanceStatus.INSUFFICIENT_FUNDS));

        verify(pendingOperationRepository, times(1)).save(pendingOperation);
        assertEquals(OperationStatus.ERROR, pendingOperation.getStatus());
        assertEquals(AccountBalanceStatus.INSUFFICIENT_FUNDS, pendingOperation.getAccountBalanceStatus());
    }

    @Test
    public void testCheckBalance_SufficientFunds_FailedStatus() {
        CheckingPaymentStatusAndBalance event = new CheckingPaymentStatusAndBalance(
                operationId, AccountBalanceStatus.SUFFICIENT_FUNDS, PaymentStatus.FAILED);

        when(pendingOperationRepository.findById(operationId)).thenReturn(Optional.of(pendingOperation));

        assertThrows(ErrorOperationException.class, () ->
                checkingBalanceService.checkBalance(event, AccountBalanceStatus.SUFFICIENT_FUNDS));

        verify(pendingOperationRepository, times(1)).save(pendingOperation);
        assertEquals(OperationStatus.ERROR, pendingOperation.getStatus());
        assertEquals(AccountBalanceStatus.SUFFICIENT_FUNDS, pendingOperation.getAccountBalanceStatus());
    }

    @Test
    public void testCheckBalance_SufficientFunds_SuccessStatus() {
        CheckingPaymentStatusAndBalance event = new CheckingPaymentStatusAndBalance(
                operationId, AccountBalanceStatus.SUFFICIENT_FUNDS, PaymentStatus.SUCCESS);

        when(pendingOperationRepository.findById(operationId)).thenReturn(Optional.of(pendingOperation));

        checkingBalanceService.checkBalance(event, AccountBalanceStatus.SUFFICIENT_FUNDS);

        verify(pendingOperationRepository, times(1)).save(pendingOperation);
        assertEquals(OperationStatus.AUTHORIZATION, pendingOperation.getStatus());
        assertEquals(AccountBalanceStatus.SUFFICIENT_FUNDS, pendingOperation.getAccountBalanceStatus());
    }

    @Test
    public void testCheckBalance_OperationNotFound() {
        CheckingPaymentStatusAndBalance event = new CheckingPaymentStatusAndBalance(
                operationId, AccountBalanceStatus.SUFFICIENT_FUNDS, PaymentStatus.SUCCESS);

        when(pendingOperationRepository.findById(operationId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () ->
                checkingBalanceService.checkBalance(event, AccountBalanceStatus.SUFFICIENT_FUNDS));
    }
    @Test
    public void testCheckBalance_UnhandledBalanceStatus() {
        CheckingPaymentStatusAndBalance event = new CheckingPaymentStatusAndBalance(
                operationId, AccountBalanceStatus.BALANCE_NOT_VERIFIED, PaymentStatus.SUCCESS);

        when(pendingOperationRepository.findById(operationId)).thenReturn(Optional.of(pendingOperation));

        checkingBalanceService.checkBalance(event, AccountBalanceStatus.BALANCE_NOT_VERIFIED);

        verify(pendingOperationRepository, never()).save(pendingOperation);
    }
}