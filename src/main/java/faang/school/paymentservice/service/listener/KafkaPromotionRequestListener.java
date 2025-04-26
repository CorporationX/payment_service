package faang.school.paymentservice.service.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.paymentservice.dto.PaymentResponse;
import faang.school.paymentservice.service.PaymentService;
import faang.school.paymentservice.service.publisher.KafkaEventPublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class KafkaPromotionRequestListener extends KafkaAbstractRequestListener {
    private final PaymentService paymentService;

    @Value("${spring.kafka.topics.promotion-payment-response}")
    private String promotionResponseTopic;

    public KafkaPromotionRequestListener(ObjectMapper objectMapper, KafkaEventPublisher kafkaEventPublisher, PaymentService paymentService) {
        super(objectMapper, kafkaEventPublisher);
        this.paymentService = paymentService;
    }

    @KafkaListener(topics = "${spring.kafka.topics.promotion-payment-request}",
            groupId = "${spring.kafka.group-id.payment-promotion-group-id}")
    public void listen(String message) {
        handleRequest(message, request -> {
            PaymentResponse paymentResponse = paymentService.createResponse(request).getBody();
            publishRequest(promotionResponseTopic, paymentResponse);
        });
    }
}
