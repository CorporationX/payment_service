package faang.school.paymentservice.service.payment;

import faang.school.paymentservice.model.Payment;
import faang.school.paymentservice.model.PaymentStatus;
import faang.school.paymentservice.repository.payment.PaymentRepository;
import faang.school.paymentservice.validator.payment.PaymentValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentStatusServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private PaymentValidator paymentValidator;

    @Mock
    private PaymentService paymentService;

    @InjectMocks
    private PaymentStatusService paymentStatusService;

    private Payment payment;
    private static final UUID PAYMENT_ID = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        payment = new Payment();
        payment.setId(PAYMENT_ID);
        payment.setStatus(PaymentStatus.AUTH_PENDING);
        payment.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    @DisplayName("Successfully updates payment status by ID")
    void whenValidPaymentIdAndStatusThenUpdateStatusSuccessfully() {
        when(paymentService.getPaymentById(PAYMENT_ID)).thenReturn(payment);
        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);

        paymentStatusService.updatePaymentStatusById(PAYMENT_ID, PaymentStatus.CONFIRM_PENDING);

        verify(paymentValidator).validateStatusForUpdateEligibility(payment);
        verify(paymentRepository).save(payment);
        assertEquals(PaymentStatus.CONFIRM_PENDING, payment.getStatus());
    }

    @Test
    @DisplayName("Throws exception when payment status is not eligible for update")
    void whenStatusNotEligibleForUpdateThenThrowsException() {
        doThrow(new IllegalArgumentException("Status not eligible for update"))
                .when(paymentValidator).validateStatusForUpdateEligibility(payment);

        assertThrows(IllegalArgumentException.class, () ->
                paymentStatusService.updatePaymentStatus(payment, PaymentStatus.CANCEL_PENDING)
        );

        verify(paymentValidator).validateStatusForUpdateEligibility(payment);
        verify(paymentRepository, never()).save(payment);
    }

    @Test
    @DisplayName("Updates payment status successfully")
    void whenValidPaymentAndStatusThenUpdatesStatus() {
        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);

        Payment updatedPayment = paymentStatusService.updatePaymentStatus(payment, PaymentStatus.CLEAR_PENDING);

        verify(paymentValidator).validateStatusForUpdateEligibility(payment);
        verify(paymentRepository).save(payment);
        assertEquals(PaymentStatus.CLEAR_PENDING, updatedPayment.getStatus());
    }
}
