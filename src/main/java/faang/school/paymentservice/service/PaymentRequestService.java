package faang.school.paymentservice.service;

import faang.school.paymentservice.dto.PaymentRequest;
import faang.school.paymentservice.dto.PaymentStatus;
import faang.school.paymentservice.entity.Payment;
import faang.school.paymentservice.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * @author Evgenii Malkov
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentRequestService {

    private final PaymentRepository paymentRepository;
    @Value("${payment.request.validityMinutes:30}")
    private long validityMinutes;

    @Transactional
    public void savePaymentRequest(PaymentRequest paymentRequest) {
        Payment payment = Payment.builder()
                .status(PaymentStatus.CREATED.toString())
                .requestId(paymentRequest.requestId())
                .amount(paymentRequest.amount())
                .currency(paymentRequest.currency().toString())
                .expiredDateTime(LocalDateTime.now().plusMinutes(validityMinutes))
                .product(paymentRequest.product().name())
                .build();
        paymentRepository.save(payment);
    }
}
