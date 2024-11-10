package faang.school.paymentservice.service.payment;

import faang.school.paymentservice.dto.payment.PaymentCreateDto;
import faang.school.paymentservice.exception.DataValidationException;
import faang.school.paymentservice.mapper.PaymentMapper;
import faang.school.paymentservice.model.Payment;
import faang.school.paymentservice.model.PaymentStatus;
import faang.school.paymentservice.repository.PaymentRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@Service
public class PaymentService {

    private final PaymentMapper paymentMapper;
    private final PaymentRepository paymentRepository;

    public Payment getPaymentById(UUID id) {
        return paymentRepository.findById(id)
                .orElseThrow(() -> new DataValidationException("Payment doesn't exist with id: " + id));
    }

    public List<Payment> getReadyForClearingPayments() {
        return paymentRepository.getReadyForClearingPayments();
    }

    @Transactional
    public Payment createAndPersistPayment(PaymentCreateDto dto) {
        Payment payment = paymentMapper.toEntity(dto);
//        payment.setIdempotencyKey(dto.getIdempotencyKey());
        payment.setStatus(PaymentStatus.AUTH_PENDING);
        payment.setCreatedAt(LocalDateTime.now());
        payment.setUpdatedAt(LocalDateTime.now());

        return paymentRepository.save(payment);
    }
}
