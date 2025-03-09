package faang.school.paymentservice.service.kafka.producer;

import faang.school.paymentservice.dto.AuthorizationMessageRequest;
import faang.school.paymentservice.dto.CancelMessageRequest;
import faang.school.paymentservice.dto.ClearingMessageRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
public class RequestPublisher {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final Map<String, String> topics;

    public RequestPublisher(
            @Qualifier("sendRequestKafkaTemplate") KafkaTemplate<String, Object> kafkaTemplate,
            @Qualifier("requestTopicName") Map<String, String> topics) {
        this.kafkaTemplate = kafkaTemplate;
        this.topics = topics;
    }

    public CompletableFuture<SendResult<String, Object>> publish(AuthorizationMessageRequest request) {
        return kafkaTemplate.send(topics.get("authRequestTopicName"), request.id().toString(), request);
    }

    public CompletableFuture<SendResult<String, Object>> publish(CancelMessageRequest request) {
        return kafkaTemplate.send(topics.get("cancelRequestTopicName"), request.paymentId().toString(), request);
    }

    public CompletableFuture<SendResult<String, Object>> publish(ClearingMessageRequest request) {
        return kafkaTemplate.send(topics.get("clearingRequestTopicName"), request.paymentId().toString(), request);
    }
}
