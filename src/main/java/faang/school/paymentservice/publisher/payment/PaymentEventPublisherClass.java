package faang.school.paymentservice.publisher.payment;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.paymentservice.dto.PaymentDto;
import faang.school.paymentservice.mapper.PaymentMapper;
import faang.school.paymentservice.model.Payment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentEventPublisherClass {
    private final RedisTemplate<String, Object> redisTemplate;
    private final PaymentMapper paymentMapper;
    private final ObjectMapper objectMapper;

    @Value("${spring.data.redis.channel.payment-event}")
    private String channel;

    public void publish(Payment payment) {
        PaymentDto paymentDto = paymentMapper.toPaymentDto(payment);
        redisTemplate.convertAndSend(channel, paymentDto);
        log.info("Send message to broker: {}", paymentDto);
    }
}
