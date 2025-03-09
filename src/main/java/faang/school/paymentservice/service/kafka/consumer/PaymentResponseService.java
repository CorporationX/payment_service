package faang.school.paymentservice.service.kafka.consumer;

import faang.school.paymentservice.dto.MessageResponse;
import faang.school.paymentservice.entity.Payment;
import faang.school.paymentservice.enums.PaymentStatus;
import faang.school.paymentservice.enums.TransferStatus;
import faang.school.paymentservice.exception.PaymentNotFoundException;
import faang.school.paymentservice.service.PaymentCheckService;
import faang.school.paymentservice.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Service
public class PaymentResponseService {
    private final PaymentService paymentService;
    private final PaymentCheckService paymentCheckService;

    private final Map<TransferStatus, PaymentStatus> statusMap = Map.of(
            TransferStatus.NOT_ENOUGH_FUNDS, PaymentStatus.ERROR_NO_ENOUGH_MONEY,
            TransferStatus.PAYMENT_NOT_FOUND, PaymentStatus.ERROR,
            TransferStatus.AUTHORIZED, PaymentStatus.AUTHORIZED,
            TransferStatus.PAYMENT_ALREADY_CANCELLED, PaymentStatus.CANCELLED,
            TransferStatus.PAYMENT_ALREADY_CLEARED, PaymentStatus.CLEARED,
            TransferStatus.CANCELLED, PaymentStatus.CANCELLED,
            TransferStatus.CLEARED, PaymentStatus.CLEARED,
            TransferStatus.ERROR, PaymentStatus.ERROR
    );

    @Transactional
    public void receiveResponse(MessageResponse response, String topicName) throws PaymentNotFoundException {
        Payment payment = paymentService.getPaymentById(response.paymentId());
        log.info("From topic {} receive result {} for payment {}", topicName, response.result(), payment.getId());
        payment.setPaymentStatus(statusMap.get(response.result()));
        paymentService.savePayment(payment);
        paymentCheckService.deleteAuthorizationMessageCounter(payment.getId());
    }
}
