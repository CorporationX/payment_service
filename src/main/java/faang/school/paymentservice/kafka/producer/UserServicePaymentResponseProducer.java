package faang.school.paymentservice.kafka.producer;

import faang.school.paymentservice.dto.PaymentResponse;
import faang.school.paymentservice.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * @author Evgenii Malkov
 */
@Component
@Slf4j
public class UserServicePaymentResponseProducer extends AbstractProducer {
    @Value("${kafka-topic.producer.user-service-payment-response}")
    private String topic;
    private final JsonUtil jsonUtil;

    public UserServicePaymentResponseProducer(KafkaTemplate<String, String> kafkaTemplate,
                                              JsonUtil jsonUtil) {
        super(kafkaTemplate);
        this.jsonUtil = jsonUtil;
    }

    public void sendPaymentInfo(PaymentResponse paymentResponse) {
        String payload = jsonUtil.parseObjectAsString(paymentResponse);
        super.send(topic, payload, null);
    }
}
