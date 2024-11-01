package faang.school.paymentservice.listener.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.paymentservice.dto.CheckingAccountBalance;
import faang.school.paymentservice.service.CheckingAccountBalanceService;
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
public class CheckingAccountBalanceKafkaListener {
    @Value("${spring.kafka.topic.checking_balance}")
    private String topic;

    private final ObjectMapper objectMapper;
    private final CheckingAccountBalanceService checkingAccountBalanceService;

    @KafkaListener(topics ="${spring.kafka.topic.checking_balance}")
    public void checkingAccountBalanceListener(String message) {
        try {
            CheckingAccountBalance event = objectMapper.readValue(message, CheckingAccountBalance.class);
            checkingAccountBalanceService.checkBalance(event, event.getStatus());

        } catch (JsonProcessingException exception) {
            log.error("Unexpected error, listen topic: {}", topic, exception);
            throw new RuntimeException(exception);
        }
    }
}
