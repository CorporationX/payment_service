package faang.school.paymentservice.service.currency;

import faang.school.paymentservice.model.Payment;
import faang.school.paymentservice.model.PaymentStatus;
import faang.school.paymentservice.publisher.payment.PaymentEventPublisher;
import faang.school.paymentservice.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentRepository paymentRepository;

    @Transactional
    @PaymentEventPublisher
    public Payment authorizePayment(Payment payment) {
        payment.setStatus(PaymentStatus.PENDING);
        return paymentRepository.save(payment);
    }
}
