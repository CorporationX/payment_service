package faang.school.paymentservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.paymentservice.config.KafkaConfig;
import faang.school.paymentservice.dto.Currency;
import faang.school.paymentservice.dto.PaymentResponse;
import faang.school.paymentservice.dto.PaymentStatus;
import faang.school.paymentservice.dto.PromotionRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final ObjectMapper objectMapper;
    private final KafkaTemplate<String, String> kafkaTemplate;

    public void paymentPromotion(ConsumerRecord<String, String> record) {
        PromotionRequest request = deserializeMessage(record.value());

        BigDecimal amount = BigDecimal.valueOf(request.budgetInDay() * request.countDays());
        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        String formattedSum = decimalFormat.format(amount);
        int verificationCode = new Random().nextInt(1000, 10000);
        String message = String.format("Dear friend! Thank you for your purchase! "
                        + "Your payment on %s %s was accepted.",
                formattedSum, Currency.USD);

        PaymentResponse paymentResponse = PaymentResponse.builder()
                .status(PaymentStatus.SUCCESS)
                .message(message)
                .verificationCode(verificationCode)
                .amount(amount)
                .currency(Currency.USD)
                .build();

        kafkaTemplate.send(KafkaConfig.PROMOTION_BOUGHT_TOPIC, record.key(), record.value());
        kafkaTemplate.send(KafkaConfig.NOTIFICATION_TOPIC, String.valueOf(request.userId()),
                convertToJson(paymentResponse));
    }

    private PromotionRequest deserializeMessage(String message) {
        try {
            return objectMapper.readValue(message, PromotionRequest.class);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка десериализации сообщения", e);
        }
    }

    private String convertToJson(Serializable request) {
        try {
            return objectMapper.writeValueAsString(request);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка сериализации JSON", e);
        }
    }
}
