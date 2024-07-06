package faang.school.paymentservice.service.payment;

import faang.school.paymentservice.config.DynamicScheduledAtTimeConfig;
import faang.school.paymentservice.dto.InvoiceDto;
import faang.school.paymentservice.dto.PaymentResponse;
import faang.school.paymentservice.dto.PaymentStatus;
import faang.school.paymentservice.mapper.PaymentMapper;
import faang.school.paymentservice.model.Payment;
import faang.school.paymentservice.publisher.PaymentEventPublisher;
import faang.school.paymentservice.repository.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {
    private static final UUID IDEMPOTENCY_KEY = UUID.randomUUID();

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private PaymentValidator paymentValidator;

    @Mock
    private PaymentMapper paymentMapper;

    @Mock
    private PaymentEventPublisher paymentEventPublisher;

    @Mock
    private DynamicScheduledAtTimeConfig scheduledAtTimeConfig;

    @InjectMocks
    private PaymentService paymentService;

    private Payment payment;
    private PaymentResponse paymentResponse;
    private InvoiceDto invoiceDto;

    @BeforeEach
    void setUp() {
        payment = Payment.builder()
                .id(1L)
                .idempotencyKey(IDEMPOTENCY_KEY)
                .status(PaymentStatus.CLEARED)
                .build();
        paymentResponse = PaymentResponse.builder()
                .id(payment.getId())
                .build();
        invoiceDto = InvoiceDto.builder()
                .idempotencyKey(payment.getIdempotencyKey())
                .build();
    }

    @Test
    public void whenCreateThenGetPaymentResponse() {
        when(paymentRepository.existsByIdempotencyKey(any())).thenReturn(false);
        when(paymentMapper.toResponse(payment)).thenReturn(paymentResponse);
        when(paymentRepository.save(any())).thenReturn(payment);
        PaymentResponse actual = paymentService.create(invoiceDto);
        verify(paymentValidator).checkInvoiceFields(invoiceDto);
        verify(scheduledAtTimeConfig).getScheduledAt();
        assertThat(actual).isEqualTo(paymentResponse);
    }

    @Test
    public void whenCancelThenGetPaymentResponse() {
        when(paymentRepository.findById(anyLong())).thenReturn(Optional.of(payment));
        when(paymentRepository.save(any())).thenReturn(payment);
        when(paymentMapper.toResponse(payment)).thenReturn(paymentResponse);
        PaymentResponse actual = paymentService.cancel(payment.getId());
        verify(paymentEventPublisher).publish(any());
        assertThat(actual).isEqualTo(paymentResponse);
    }

    @Test
    public void whenClearThenGetPaymentResponse() {
        when(paymentRepository.findById(anyLong())).thenReturn(Optional.of(payment));
        when(paymentRepository.save(any())).thenReturn(payment);
        when(paymentMapper.toResponse(payment)).thenReturn(paymentResponse);
        PaymentResponse actual = paymentService.clear(payment.getId());
        verify(paymentEventPublisher).publish(any());
        assertThat(actual).isEqualTo(paymentResponse);
    }

    @Test
    public void whenSuccessPaymentResponse() {
        when(paymentRepository.findById(anyLong())).thenReturn(Optional.of(payment));
        when(paymentRepository.save(any())).thenReturn(payment);
        when(paymentMapper.toResponse(payment)).thenReturn(paymentResponse);
        PaymentResponse actual = paymentService.success(payment.getId());
        assertThat(actual).isEqualTo(paymentResponse);
    }
}