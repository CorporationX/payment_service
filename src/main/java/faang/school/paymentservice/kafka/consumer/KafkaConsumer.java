package faang.school.paymentservice.kafka.consumer;

import faang.school.paymentservice.kafka.dto.KafkaAuthorizationResponseDto;
import faang.school.paymentservice.kafka.dto.KafkaCancelResponseDto;
import faang.school.paymentservice.kafka.dto.KafkaClearingResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaConsumer {

    private final KafkaConsumerService kafkaConsumerService;

    @KafkaListener(
            topics = "${spring.kafka.topics.payment.authorization-response}",
            groupId = "authorization-response",
            concurrency = "${spring.kafka.consumer.concurrency}")
    public void authorizationResponseListener(ConsumerRecord<String, KafkaAuthorizationResponseDto> consumerRecord, Acknowledgment ack) {
        log.info("Get Kafka event authorization response: {}", consumerRecord);
        kafkaConsumerService.handleAuthorizationResponse(consumerRecord.value());
        ack.acknowledge();
    }

    @KafkaListener(
            topics = "${spring.kafka.topics.payment.clearing-response}",
            groupId = "clearing-response",
            concurrency = "${spring.kafka.consumer.concurrency}")
    public void clearingResponseListener(ConsumerRecord<String, KafkaClearingResponseDto> consumerRecord, Acknowledgment ack) {
        log.info("Get Kafka event clearing response: {}", consumerRecord);
        kafkaConsumerService.handleClearingResponse(consumerRecord.value());
        ack.acknowledge();
    }

    @KafkaListener(
            topics = "${spring.kafka.topics.payment.cancel-response}",
            groupId = "clearing-response",
            concurrency = "${spring.kafka.consumer.concurrency}")
    public void cancelResponseListener(ConsumerRecord<String, KafkaCancelResponseDto> consumerRecord, Acknowledgment ack) {
        log.info("Get Kafka event cancel response: {}", consumerRecord);
        kafkaConsumerService.handleCancelResponse(consumerRecord.value());
        ack.acknowledge();
    }
}
