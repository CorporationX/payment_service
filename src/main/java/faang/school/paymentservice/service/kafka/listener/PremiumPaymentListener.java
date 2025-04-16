package faang.school.paymentservice.service.kafka.listener;

import faang.school.paymentservice.dto.PaymentResponseDto;
import faang.school.paymentservice.dto.exchange.ExchangeRequestDto;
import faang.school.paymentservice.dto.exchange.ExchangeResponseDto;
import faang.school.paymentservice.dto.premium.PremiumPaymentRequestDto;
import faang.school.paymentservice.dto.premium.PremiumPaymentResponseDto;
import faang.school.paymentservice.service.kafka.publisher.KafkaPublisher;
import faang.school.paymentservice.service.payment.PaymentService;
import faang.school.paymentservice.utils.JsonUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Header;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;

import static faang.school.paymentservice.messages.ErrorMessages.FAILED_TO_ACKNOWLEDGE_KAFKA_MESSAGE;

@Component
@Slf4j
@RequiredArgsConstructor
public class PremiumPaymentListener {
    public static final String RECEIVED_MESSAGE_FROM_KAFKA = "Received message from kafka: {}";

    private final PaymentService paymentService;
    private final JsonUtils jsonUtils;
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
        PremiumPaymentRequestDto premiumPaymentRequest =
                jsonUtils.deserialize(message, PremiumPaymentRequestDto.class);

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

        acknowledgeMessage(acknowledgment);
    }

    @KafkaListener(
            topics = "${spring.kafka.consumer.topics.premium.price-request-topic}",
            groupId = "${spring.kafka.consumer.groups.premium.price-request-group}"
    )
    @Transactional(transactionManager = "kafkaTransactionManager")
    public void premiumPriceRequestListener(ConsumerRecord<String, String> record, Acknowledgment acknowledgment) {
        String message = record.value();
        log.info("Received message from Kafka: {}", message);
        ExchangeRequestDto exchangeRequest = jsonUtils.deserialize(message, ExchangeRequestDto.class);
        String correlationId = null;

        Header header = record.headers().lastHeader(premiumPriceCorrelationId);
        if (header != null) {
            correlationId = new String(header.value(), StandardCharsets.UTF_8);
        } else {
            log.warn("No premium-payment-correlation-id header found in the request");
        }
        ExchangeResponseDto exchangeResponse = paymentService.convertCurrency(exchangeRequest);
        kafkaPublisher.sendInTransaction(exchangeResponse, priceResponseTopic,
                premiumPriceCorrelationId, correlationId);

        acknowledgeMessage(acknowledgment);
    }

    private void acknowledgeMessage(Acknowledgment acknowledgment) {
        try {
            acknowledgment.acknowledge();
        } catch (Exception e) {
            log.error(FAILED_TO_ACKNOWLEDGE_KAFKA_MESSAGE, e);
            throw new RuntimeException(e);
        }
    }
}
