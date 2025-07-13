package faang.school.paymentservice.facade.payment;

import faang.school.paymentservice.entity.payment.PaymentOperation;
import faang.school.paymentservice.event.payment.PaymentAuthorizationEventDto;
import faang.school.paymentservice.mapper.payment.PaymentOperationKafkaMapper;
import faang.school.paymentservice.publisher.payment.PaymentKafkaPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class PaymentOperationKafkaPublisherFacade {
    private final PaymentKafkaPublisher paymentKafkaPublisher;
    private final PaymentOperationKafkaMapper paymentOperationKafkaMapper;

    @Async("sendKafkaMessageExecutor")
    public void createPaymentAuthorizationEvent(PaymentOperation paymentOperation) {
        PaymentAuthorizationEventDto paymentAuthorizationEventDto =
                paymentOperationKafkaMapper.toPaymentAuthorizationEventDto(paymentOperation);
        log.info("Mapping PaymentOperation entity to PaymentAuthorizationEventDto." +
                        "Entity content: {}. DTO content: {}.", paymentOperation, paymentAuthorizationEventDto);

        paymentKafkaPublisher.sendMessage(paymentAuthorizationEventDto);
    }
}
