package faang.school.paymentservice.listener.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.paymentservice.dto.CheckingPaymentStatusAndBalance;
import faang.school.paymentservice.dto.PaymentStatusResponseDto;
import faang.school.paymentservice.service.CheckPaymentStatusService;
import faang.school.paymentservice.service.CheckingBalanceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "app", name = "messaging", havingValue = "kafka")
@Component
public class ErrorPaymentKafkaListener {
    @Value("${spring.kafka.topic.error-payment-response}")
    private String topic;

    private final ObjectMapper objectMapper;
    private final CheckPaymentStatusService checkPaymentStatusService;

    @KafkaListener(topics ="${spring.kafka.topic.error-payment-response}")
    public void checkingPaymentStatus(String message) {
        try {
            PaymentStatusResponseDto event = objectMapper.readValue(message, PaymentStatusResponseDto.class);
            checkPaymentStatusService.checkPaymentStatus(event);
        } catch (JsonProcessingException exception) {
            log.error("Unexpected error, listen topic: {}", topic, exception);
            throw new RuntimeException(exception);
        }
    }
}
