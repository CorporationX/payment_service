package faang.school.paymentservice.service.kafka.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.paymentservice.dto.MessageResponse;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class ResponseListener {
    private final PaymentResponseService paymentResponseService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = {
            "${spring.kafka.topics.payment-topics.clearing.response.topic-name}",
            "${spring.kafka.topics.payment-topics.cancel.response.topic-name}",
            "${spring.kafka.topics.payment-topics.authorization.response.topic-name}"
    },
            groupId = "${spring.kafka.topics.payment-topics.response-listener-group}",
            containerFactory = "paymentKafkaListenerContainerFactory")
    void listener(ConsumerRecord<String, Object> kafkaEvent) {
        MessageResponse response = objectMapper.convertValue(kafkaEvent.value(), MessageResponse.class);
        String topicName = kafkaEvent.topic();
        paymentResponseService.receiveResponse(response, topicName);
    }
}
