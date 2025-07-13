package faang.school.paymentservice.facade.payment;

import faang.school.paymentservice.entity.payment.PaymentOperation;
import faang.school.paymentservice.entity.payment.PaymentOperationStatus;
import faang.school.paymentservice.event.payment.FailedPaymentAuthorizationEventDto;
import faang.school.paymentservice.event.payment.SuccessPaymentAuthorizationEventDto;
import faang.school.paymentservice.service.payment.PaymentOperationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class PaymentOperationKafkaListenerFacade {
    private PaymentOperationService paymentOperationService;

    // TODO: что делать с переменными ответа
    public void completeAuthorization(SuccessPaymentAuthorizationEventDto paymentEvent) {
        PaymentOperation paymentOperation = paymentOperationService.updatePaymentOperationStatus(
                paymentEvent.operationToken(),
                paymentEvent.operationId(),
                paymentEvent.timestamp(),
                PaymentOperationStatus.AUTHORIZED,
                paymentEvent.detail()
        );
    }

    public void cancelAuthorization(FailedPaymentAuthorizationEventDto paymentEvent) {
        PaymentOperation paymentOperation = paymentOperationService.updatePaymentOperationStatus(
                paymentEvent.operationToken(),
                paymentEvent.operationId(),
                paymentEvent.timestamp(),
                PaymentOperationStatus.AUTHORIZATION_FAILED,
                paymentEvent.detail()
        );
    }
}
