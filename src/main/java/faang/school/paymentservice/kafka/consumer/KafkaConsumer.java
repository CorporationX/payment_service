package faang.school.paymentservice.kafka.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.paymentservice.kafka.dto.AuthorizationKafkaResponseDto;
import faang.school.paymentservice.kafka.dto.CancelKafkaResponseDto;
import faang.school.paymentservice.kafka.dto.ClearingKafkaResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    public void authorizationResponseListener(@Payload Map<String, Object> responseDto, Acknowledgment ack) {
        AuthorizationKafkaResponseDto authorizationKafkaResponseDto = objectMapper.convertValue(responseDto, AuthorizationKafkaResponseDto.class);
        log.info("Get Kafka event authorization response: {}", responseDto);
        kafkaConsumerService.handleAuthorizationResponse(authorizationKafkaResponseDto);
        ack.acknowledge();
    }

    @KafkaListener(
            topics = "${spring.kafka.topics.payment.clearing-response}",
            groupId = "clearing-response",
            concurrency = "${spring.kafka.consumer.concurrency}",
            containerFactory = "paymentKafkaListenerContainerFactory")
    public void clearingResponseListener(@Payload Map<String, Object> responseDto, Acknowledgment ack) {
        ClearingKafkaResponseDto clearingKafkaResponseDto = objectMapper.convertValue(responseDto, ClearingKafkaResponseDto.class);
        log.info("Get Kafka event clearing response: {}", responseDto);
        kafkaConsumerService.handleClearingResponse(clearingKafkaResponseDto);
        ack.acknowledge();
    }

    @KafkaListener(
            topics = "${spring.kafka.topics.payment.cancel-response}",
            groupId = "cancel-response",
            concurrency = "${spring.kafka.consumer.concurrency}",
            containerFactory = "paymentKafkaListenerContainerFactory")
    public void cancelResponseListener(@Payload Map<String, Object> responseDto, Acknowledgment ack) {
        CancelKafkaResponseDto cancelKafkaResponseDto = objectMapper.convertValue(responseDto, CancelKafkaResponseDto.class);
        log.info("Get Kafka event cancel response: {}", responseDto);
        kafkaConsumerService.handleCancelResponse(cancelKafkaResponseDto);
        ack.acknowledge();
    }
}
