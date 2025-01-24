package faang.school.paymentservice.listener;

import faang.school.paymentservice.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import static faang.school.paymentservice.config.KafkaConfig.PAYMENT_PROMOTION_TOPIC;

@Component
@RequiredArgsConstructor
public class PromotionListener {

    private final PaymentService paymentService;

    @KafkaListener(topics = PAYMENT_PROMOTION_TOPIC)
    public void processPaymentPromotionTopic(ConsumerRecord<String, String> record, Acknowledgment ack) {
        paymentService.paymentPromotion(record);
        ack.acknowledge();
    }
}