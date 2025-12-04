package faang.school.paymentservice.kafka.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.paymentservice.kafka.dto.KafkaAuthorizationResponseDto;
import faang.school.paymentservice.kafka.dto.KafkaCancelResponseDto;
import faang.school.paymentservice.kafka.dto.KafkaClearingResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaConsumer {

    private final KafkaConsumerService kafkaConsumerService;
    private final ObjectMapper objectMapper;

    @KafkaListener(
            topics = "${spring.kafka.topics.payment.authorization-response}",
            groupId = "authorization-response",
            concurrency = "${spring.kafka.consumer.concurrency}",
            containerFactory = "paymentKafkaListenerContainerFactory")
    public void authorizationResponseListener(@Payload Map<String, Object> consumerRecord, Acknowledgment ack) {
        KafkaAuthorizationResponseDto kafkaAuthorizationResponseDto = objectMapper.convertValue(consumerRecord, KafkaAuthorizationResponseDto.class);
        log.info("Get Kafka event authorization response: {}", consumerRecord);
        kafkaConsumerService.handleAuthorizationResponse(kafkaAuthorizationResponseDto);
        ack.acknowledge();
    }

    @KafkaListener(
            topics = "${spring.kafka.topics.payment.clearing-response}",
            groupId = "clearing-response",
            concurrency = "${spring.kafka.consumer.concurrency}",
            containerFactory = "paymentKafkaListenerContainerFactory")
    public void clearingResponseListener(@Payload Map<String, Object> consumerRecord, Acknowledgment ack) {
        KafkaClearingResponseDto kafkaClearingResponseDto = objectMapper.convertValue(consumerRecord, KafkaClearingResponseDto.class);
        log.info("Get Kafka event clearing response: {}", consumerRecord);
        kafkaConsumerService.handleClearingResponse(kafkaClearingResponseDto);
        ack.acknowledge();
    }

    @KafkaListener(
            topics = "${spring.kafka.topics.payment.cancel-response}",
            groupId = "clearing-response",
            concurrency = "${spring.kafka.consumer.concurrency}",
            containerFactory = "paymentKafkaListenerContainerFactory")
    public void cancelResponseListener(ConsumerRecord<String, Object> consumerRecord, Acknowledgment ack) {
        KafkaCancelResponseDto kafkaCancelResponseDto = objectMapper.convertValue(consumerRecord, KafkaCancelResponseDto.class);
        log.info("Get Kafka event cancel response: {}", consumerRecord);
        kafkaConsumerService.handleCancelResponse(kafkaCancelResponseDto);
        ack.acknowledge();
    }
}
