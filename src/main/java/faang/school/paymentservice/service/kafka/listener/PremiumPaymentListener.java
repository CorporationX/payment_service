package faang.school.paymentservice.service.kafka.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.paymentservice.dto.PaymentResponseDto;
import faang.school.paymentservice.dto.exchange.ExchangeRequestDto;
import faang.school.paymentservice.dto.exchange.ExchangeResponseDto;
import faang.school.paymentservice.dto.premium.PremiumPaymentRequestDto;
import faang.school.paymentservice.dto.premium.PremiumPaymentResponseDto;
import faang.school.paymentservice.service.kafka.publisher.KafkaPublisher;
import faang.school.paymentservice.service.payment.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Header;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;

import static faang.school.paymentservice.messages.ErrorMessages.FAILED_TO_ACKNOWLEDGE_KAFKA_MESSAGE;

@Component
@Slf4j
@RequiredArgsConstructor
public class PremiumPaymentListener {
    public static final String RECEIVED_MESSAGE_FROM_KAFKA = "Received message from kafka: {}";
    private final PaymentService paymentService;
    private final ObjectMapper objectMapper;
    private final KafkaPublisher kafkaPublisher;

    @Value("${spring.kafka.producer.topics.premium.payment-response-topic}")
    private String paymentResponseTopic;

    @Value("${spring.kafka.producer.topics.premium.price-response-topic}")
    private String priceResponseTopic;

    @Value("${spring.kafka.consumer.correlation.premium-price}")
    private String premiumPriceCorrelationId;

    @Value("${spring.kafka.consumer.correlation.premium-payment}")
    private String premiumPaymentCorrelationId;

    @KafkaListener(
            topics = "${spring.kafka.consumer.topics.premium.payment-request-topic}",
            groupId = "${spring.kafka.consumer.groups.premium.payment-request-group}"
    )
    @Transactional(transactionManager = "kafkaTransactionManager")
    public void premiumPaymentRequestListener(ConsumerRecord<String, String> record, Acknowledgment acknowledgment) {
        String message = record.value();
        log.info(RECEIVED_MESSAGE_FROM_KAFKA, message);
        PremiumPaymentRequestDto premiumPaymentRequest;
        try {
            premiumPaymentRequest = objectMapper.readValue(message,
                    PremiumPaymentRequestDto.class);
        } catch (JsonProcessingException e) {
            log.error("Error while deserializing PremiumPaymentResponseDto", e);
            throw new RuntimeException(e);
        }
        PaymentResponseDto paymentResponse = paymentService.sendPayment(
                premiumPaymentRequest.getPaymentRequestDto()).getBody();

        PremiumPaymentResponseDto premiumPaymentResponseDto = PremiumPaymentResponseDto.builder()
                .premiumRequestDto(premiumPaymentRequest.getPremiumRequestDto())
                .paymentResponseDto(paymentResponse)
                .byUser(premiumPaymentRequest.isByUser())
                .build();

        String correlationId = null;
        Header header = record.headers().lastHeader(premiumPaymentCorrelationId);
        if (header != null) {
            correlationId = new String(header.value(), StandardCharsets.UTF_8);
        } else {
            log.warn("No premium-price-correlation-id header found in the request");
        }

        kafkaPublisher.sendInTransaction(premiumPaymentResponseDto, paymentResponseTopic,
                premiumPaymentCorrelationId, correlationId);

        try {
            acknowledgment.acknowledge();
        } catch (Exception e) {
            log.error(FAILED_TO_ACKNOWLEDGE_KAFKA_MESSAGE, e);
            throw new RuntimeException(e);
        }
    }

    @KafkaListener(
            topics = "${spring.kafka.consumer.topics.premium.price-request-topic}",
            groupId = "${spring.kafka.consumer.groups.premium.price-request-group}"
    )
    @Transactional(transactionManager = "kafkaTransactionManager")
    public void premiumPriceRequestListener(ConsumerRecord<String, String> record, Acknowledgment acknowledgment) {
        String message = record.value();
        log.info("Received message from Kafka: {}", message);
        ExchangeRequestDto exchangeRequest;
        try {
            exchangeRequest = objectMapper.readValue(message, ExchangeRequestDto.class);
        } catch (JsonProcessingException e) {
            log.error("Error while deserializing ExchangeRequestDto", e);
            throw new RuntimeException(e);
        }

        String correlationId = null;
        Header header = record.headers().lastHeader(premiumPriceCorrelationId);
        if (header != null) {
            correlationId = new String(header.value(), StandardCharsets.UTF_8);
        } else {
            log.warn("No premium-payment-correlation-id header found in the request");
        }
        ExchangeResponseDto exchangeResponse = new ExchangeResponseDto(
                exchangeRequest.getToCurrency(),
                BigDecimal.TEN,
                exchangeRequest.getUserId()
        );

        kafkaPublisher.sendInTransaction(exchangeResponse, priceResponseTopic,
                premiumPriceCorrelationId, correlationId);

        try {
            acknowledgment.acknowledge();
        } catch (Exception e) {
            log.error("Failed to acknowledge Kafka message", e);
            throw new RuntimeException(e);
        }
    }
}
