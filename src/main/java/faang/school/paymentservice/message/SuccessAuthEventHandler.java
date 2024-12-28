package faang.school.paymentservice.message;

import faang.school.paymentservice.dto.payment.AuthorizationEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
@Slf4j
public class SuccessAuthEventHandler {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void handle(AuthorizationEvent event){

        kafkaTemplate.send("clearing-payment-topic", event);

    }

}
