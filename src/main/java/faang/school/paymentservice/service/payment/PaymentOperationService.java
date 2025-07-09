package faang.school.paymentservice.service.payment;

import faang.school.paymentservice.entity.payment.PaymentOperation;
import faang.school.paymentservice.entity.payment.PaymentOperationStatus;
import faang.school.paymentservice.facade.payment.PaymentOperationKafkaFacade;
import faang.school.paymentservice.repository.payment.PaymentOperationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentOperationService {
    private final PaymentOperationRepository paymentOperationRepository;
    private final PaymentOperationKafkaFacade paymentOperationKafkaFacade;

    @Transactional
    public PaymentOperation authorizePayment(PaymentOperation paymentOperation) {

        // TODO: логика валидации идемпотентности
        paymentOperation.setOperationToken(UUID.randomUUID());
        paymentOperation.setStatus(PaymentOperationStatus.PENDING);
        // TODO: в конфиг
        paymentOperation.setClearScheduledAt(LocalDateTime.now().plusMinutes(15));

        PaymentOperation savedPaymentOperation = paymentOperationRepository.save(paymentOperation);
        log.info("Payment operation {} has been save", savedPaymentOperation);

        paymentOperationKafkaFacade.createPaymentOperationEvent(savedPaymentOperation);

        return savedPaymentOperation;
    }
}
