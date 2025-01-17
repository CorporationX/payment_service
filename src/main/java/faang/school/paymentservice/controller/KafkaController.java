package faang.school.paymentservice.controller;

import faang.school.paymentservice.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Controller;

import static faang.school.paymentservice.config.KafkaConfig.PAYMENT_PROCESSING_GROUP;
import static faang.school.paymentservice.config.KafkaConfig.PAYMENT_PROMOTION_TOPIC;

@Controller
@RequiredArgsConstructor
public class KafkaController {

    private final PaymentService paymentService;

    @KafkaListener(topics = PAYMENT_PROMOTION_TOPIC, groupId = PAYMENT_PROCESSING_GROUP)
    public void processPaymentPromotionTopic(ConsumerRecord<String, String> record, Acknowledgment ack) {
        paymentService.paymentPromotion(record);
        ack.acknowledge();
    }
}
