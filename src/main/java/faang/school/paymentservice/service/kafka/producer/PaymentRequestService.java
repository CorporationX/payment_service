package faang.school.paymentservice.service.kafka.producer;

import faang.school.paymentservice.dto.AuthorizationMessageRequest;
import faang.school.paymentservice.dto.CancelMessageRequest;
import faang.school.paymentservice.dto.ClearingMessageRequest;
import faang.school.paymentservice.entity.Payment;
import faang.school.paymentservice.enums.CancelType;
import faang.school.paymentservice.enums.ClearingType;
import faang.school.paymentservice.exception.PaymentOperationException;
import faang.school.paymentservice.service.PaymentCheckService;
import faang.school.paymentservice.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.kafka.KafkaException;
import org.springframework.kafka.support.SendResult;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class PaymentRequestService {
    private final PaymentService paymentService;
    private final PaymentCheckService paymentCheckService;
    private final RequestPublisher requestPublisher;

    @Async("scheduledSendAuthExecutorService")
    @Transactional(timeout = 60)
    public void sendAuthorizePayments() {
        List<Payment> newPayments = paymentService.getNewPayments();
        newPayments.forEach(payment -> {
            try {
                if (paymentCheckService.canAuthorizePayment(payment)) {
                    AuthorizationMessageRequest request = getAuthorizationMessageRequest(payment);

                    SendResult<String, Object> result = requestPublisher.publish(request).join();
                    RecordMetadata recordMetadata = result.getRecordMetadata();

                    log.info("Authorization message send to topic={}, partition={}, offset={}, timestamp={}, paymentId={}",
                            recordMetadata.topic(), recordMetadata.partition(), recordMetadata.offset(),
                            recordMetadata.timestamp(), payment.getId());
                }
                paymentCheckService.incrementAuthorizationMessageCount(payment.getId());
            } catch (PaymentOperationException e) {
                paymentService.setPaymentErrorStatus(payment.getId());
            } catch (KafkaException e) {
                log.warn("Send authorization message failed: {}", e.getMessage());
            }
        });
    }

    @Async("scheduledSendClearingExecutorService")
    @Transactional(timeout = 60)
    public void sendClearingPayments() {
        List<Payment> authorizedPayments = paymentService.getPaymentsByClearing();
        authorizedPayments.forEach(payment -> {
            try {
                if (paymentCheckService.isPaymentClearable(payment)) {
                    ClearingMessageRequest request = getClearingMessageRequest(payment);

                    SendResult<String, Object> result = requestPublisher.publish(request).join();
                    RecordMetadata recordMetadata = result.getRecordMetadata();

                    log.info("Clearing message send to topic={}, partition={}, offset={}, timestamp={}, paymentId={}",
                            recordMetadata.topic(), recordMetadata.partition(), recordMetadata.offset(),
                            recordMetadata.timestamp(), payment.getId());
                }
                paymentCheckService.incrementClearingMessageCount(payment.getId());
            } catch (PaymentOperationException e) {
                paymentService.setPaymentErrorStatus(payment.getId());
            } catch (KafkaException e) {
                log.warn("Send clearing message failed: {}", e.getMessage());
            }
        });
    }

    @Async("scheduledSendCancelExecutorService")
    @Transactional(timeout = 60)
    public void sendCancelPayments() {
        List<Payment> cancelPayments = paymentService.getCancelPayments();
        cancelPayments.forEach(payment -> {
            try {
                if (paymentCheckService.isPaymentCancellable(payment)) {
                    CancelMessageRequest request = getCancelMessageRequest(payment);
                    SendResult<String, Object> result = requestPublisher.publish(request).join();
                    RecordMetadata recordMetadata = result.getRecordMetadata();

                    log.info("Cancel message send to topic={}, partition={}, offset={}, timestamp={}, paymentId={}",
                            recordMetadata.topic(), recordMetadata.partition(), recordMetadata.offset(),
                            recordMetadata.timestamp(), payment.getId());
                }
                paymentCheckService.incrementCancelMessageCount(payment.getId());
            } catch (PaymentOperationException e) {
                paymentService.setPaymentErrorStatus(payment.getId());
            } catch (KafkaException e) {
                log.warn("Send cancel message failed: {}", e.getMessage());
            }
        });
    }

    private AuthorizationMessageRequest getAuthorizationMessageRequest(Payment payment) {
        return new AuthorizationMessageRequest(
                payment.getId(),
                payment.getSenderAccountNumber(),
                payment.getReceiverAccountNumber(),
                payment.getCurrency(),
                payment.getAmount(),
                payment.getPaymentType()
        );
    }

    private CancelMessageRequest getCancelMessageRequest(Payment payment) {
        return new CancelMessageRequest(payment.getId(), CancelType.CANCEL_BY_USER);
    }

    private ClearingMessageRequest getClearingMessageRequest(Payment payment) {
        return new ClearingMessageRequest(payment.getId(), ClearingType.SCHEDULER_CLEARING);
    }

}
