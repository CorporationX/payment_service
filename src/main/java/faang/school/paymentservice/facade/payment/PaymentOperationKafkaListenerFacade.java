package faang.school.paymentservice.facade.payment;

import faang.school.paymentservice.entity.payment.PaymentOperationStatus;
import faang.school.paymentservice.event.payment.FailedPaymentAuthorizationEventDto;
import faang.school.paymentservice.event.payment.FailedPaymentCancelEventDto;
import faang.school.paymentservice.event.payment.FailedPaymentClearingEventDto;
import faang.school.paymentservice.event.payment.SuccessPaymentAuthorizationEventDto;
import faang.school.paymentservice.event.payment.SuccessPaymentCancelEventDto;
import faang.school.paymentservice.event.payment.SuccessPaymentClearingEventDto;
import faang.school.paymentservice.service.payment.PaymentOperationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class PaymentOperationKafkaListenerFacade {
    private PaymentOperationService paymentOperationService;
    public void onAuthorizationCompleted(SuccessPaymentAuthorizationEventDto paymentEvent) {
        paymentOperationService.updatePaymentOperationStatus(
                paymentEvent.operationToken(),
                paymentEvent.timestamp(),
                PaymentOperationStatus.AUTHORIZED,
                paymentEvent.detail()
        );
    }

    public void onAuthorizationFailed(FailedPaymentAuthorizationEventDto paymentEvent) {
        paymentOperationService.updatePaymentOperationStatus(
                paymentEvent.operationToken(),
                paymentEvent.timestamp(),
                PaymentOperationStatus.AUTHORIZATION_FAILED,
                paymentEvent.detail()
        );
    }

    public void onClearingCompleted(SuccessPaymentClearingEventDto paymentEvent) {
        paymentOperationService.updatePaymentOperationStatus(
                paymentEvent.operationToken(),
                paymentEvent.timestamp(),
                PaymentOperationStatus.CLEARED,
                paymentEvent.detail()
        );
    }

    public void onClearingFailed(FailedPaymentClearingEventDto paymentEvent) {
        paymentOperationService.updatePaymentOperationStatus(
                paymentEvent.operationToken(),
                paymentEvent.timestamp(),
                PaymentOperationStatus.CLEAR_FAILED,
                paymentEvent.detail()
        );
    }

    public void onCancellationCompleted(SuccessPaymentCancelEventDto paymentEvent) {
        paymentOperationService.updatePaymentOperationStatus(
                paymentEvent.operationToken(),
                paymentEvent.timestamp(),
                PaymentOperationStatus.CANCELED,
                paymentEvent.detail()
        );
    }

    public void onCancellationFailed(FailedPaymentCancelEventDto paymentEvent) {
        paymentOperationService.updatePaymentOperationStatus(
                paymentEvent.operationToken(),
                paymentEvent.timestamp(),
                PaymentOperationStatus.CANCELLATION_FAILED,
                paymentEvent.detail()
        );
    }
}
