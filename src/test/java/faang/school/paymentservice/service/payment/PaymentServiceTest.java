package faang.school.paymentservice.service.payment;

import faang.school.paymentservice.config.DynamicScheduledAtTimeConfig;
import faang.school.paymentservice.dto.InvoiceDto;
import faang.school.paymentservice.dto.PaymentDto;
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
import org.yaml.snakeyaml.tokens.Token;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {
/*    private static final UUID IDEMPOTENCY_KEY = UUID.randomUUID();

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

    @Test
    public void testCreate() {
        // Setup
        InvoiceDto invoiceDto = new InvoiceDto();
        invoiceDto.setIdempotencyKey(IDEMPOTENCY_KEY);
        Payment payment = new Payment();
        when(paymentRepository.findByIdempotencyKey(any())).thenReturn(Optional.empty());
        when(paymentMapper.toDto(payment)).thenReturn(new PaymentDto());
        // Test
        PaymentDto actual = paymentService.create(invoiceDto);

        // Verify
        verify(paymentRepository).save(any(Payment.class));
        // Add more verifications as needed
    }

    @Test
    public void testCancel() {
        // Setup
        Long paymentId = 1L;
        Payment payment = new Payment();
        payment.setId(paymentId);
        when(paymentRepository.findById(anyLong())).thenReturn(Optional.of(payment));
        when(paymentMapper.toDto(payment)).thenReturn(PaymentDto.builder().id(paymentId).build());

        // Test
        PaymentDto result = paymentService.cancel(paymentId);

        // Verify
        verify(paymentRepository).save(any(Payment.class));
        // Add more verifications as needed
    }

    @Test
    public void testClear() {
        Long paymentId = 1L;
        Payment payment = new Payment();
        when(paymentRepository.findById(anyLong())).thenReturn(Optional.of(payment));
        when(paymentMapper.toDto(payment)).thenReturn(new PaymentDto());
        PaymentDto result = paymentService.clear(paymentId);
        verify(paymentRepository).save(any(Payment.class));
    }*/
}