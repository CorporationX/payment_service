package faang.school.paymentservice.service.payment;

import faang.school.paymentservice.model.Payment;
import faang.school.paymentservice.model.PaymentStatus;
import faang.school.paymentservice.repository.PaymentRepository;
import faang.school.paymentservice.validator.payment.PaymentValidator;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@AllArgsConstructor
public class PaymentStatusService {

    private final PaymentRepository paymentRepository;
    private final PaymentValidator paymentValidator;
    private final PaymentService paymentService;

    @Transactional
    public void updatePaymentStatusById(UUID paymentId, PaymentStatus status) {
        Payment payment = paymentService.getPaymentById(paymentId);

        updatePaymentStatus(payment, status);
    }

    @Transactional
    public Payment updatePaymentStatus(Payment payment, PaymentStatus status) {
        paymentValidator.validateStatusForUpdateEligibility(payment);

        payment.setStatus(status);
        payment.setUpdatedAt(LocalDateTime.now());

        return paymentRepository.save(payment);
    }
}
