package faang.school.paymentservice.service.payment;

import faang.school.paymentservice.dto.payment.PaymentCreateDto;
import faang.school.paymentservice.exception.DataValidationException;
import faang.school.paymentservice.mapper.PaymentMapper;
import faang.school.paymentservice.model.Payment;
import faang.school.paymentservice.model.PaymentStatus;
import faang.school.paymentservice.repository.payment.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    private static final UUID PAYMENT_ID = UUID.randomUUID();
    private static final String CURRENCY = "USD";
    private static final String CATEGORY = "PREMIUM_SUBSCRIPTION";

    @Mock
    private PaymentMapper paymentMapper;

    @Mock
    private PaymentRepository paymentRepository;

    @InjectMocks
    private PaymentService paymentService;

    private Payment payment;
    private PaymentCreateDto paymentCreateDto;

    @BeforeEach
    void setUp() {
        payment = new Payment();
        payment.setId(PAYMENT_ID);
        payment.setStatus(PaymentStatus.AUTH_PENDING);
        payment.setCreatedAt(LocalDateTime.now());
        payment.setUpdatedAt(LocalDateTime.now());

        paymentCreateDto = new PaymentCreateDto();
        paymentCreateDto.setAmount(BigDecimal.valueOf(100.00));
        paymentCreateDto.setSourceAccountId(UUID.randomUUID());
        paymentCreateDto.setTargetAccountId(UUID.randomUUID());
        paymentCreateDto.setCurrency(CURRENCY);
        paymentCreateDto.setCategory(CATEGORY);
    }

    @Test
    @DisplayName("Returns payment by ID when it exists")
    void whenPaymentExistsThenReturnPaymentById() {
        when(paymentRepository.findById(PAYMENT_ID)).thenReturn(Optional.of(payment));

        Payment result = paymentService.getPaymentById(PAYMENT_ID);

        assertEquals(payment, result);
        verify(paymentRepository).findById(PAYMENT_ID);
    }

    @Test
    @DisplayName("Throws exception when payment by ID does not exist")
    void whenPaymentDoesNotExistThenThrowException() {
        when(paymentRepository.findById(PAYMENT_ID)).thenReturn(Optional.empty());

        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> paymentService.getPaymentById(PAYMENT_ID));

        assertEquals("Payment doesn't exist with id: " + PAYMENT_ID, exception.getMessage());
        verify(paymentRepository).findById(PAYMENT_ID);
    }

    @Test
    @DisplayName("Returns list of ready for clearing payments")
    void whenPaymentsReadyForClearingThenReturnList() {
        List<Payment> readyPayments = List.of(payment);
        when(paymentRepository.getReadyForClearingPayments()).thenReturn(readyPayments);

        List<Payment> result = paymentService.getReadyForClearingPayments();

        assertEquals(readyPayments, result);
        verify(paymentRepository).getReadyForClearingPayments();
    }

    @Test
    @DisplayName("Creates and persists new payment")
    void whenValidPaymentDtoThenCreateAndPersistPayment() {
        when(paymentMapper.toEntity(paymentCreateDto)).thenReturn(payment);
        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);

        Payment result = paymentService.createAndPersistPayment(paymentCreateDto);

        assertEquals(PaymentStatus.AUTH_PENDING, result.getStatus());
        assertNotNull(result.getCreatedAt());
        assertNotNull(result.getUpdatedAt());
        verify(paymentMapper).toEntity(paymentCreateDto);
        verify(paymentRepository).save(payment);
    }
}

