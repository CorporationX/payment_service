package faang.school.paymentservice.publisher.payment;


import faang.school.paymentservice.dto.payment.PaymentEventDto;

import faang.school.paymentservice.mapper.PaymentMapper;
import faang.school.paymentservice.model.Payment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentEventPublisher {
    private final RedisTemplate<String, Object> redisTemplate;
    private final PaymentMapper paymentMapper;

    public void publish(String topic, Payment payment) {
        PaymentEventDto paymentEventDto = paymentMapper.toPaymentEventDto(payment);
        redisTemplate.convertAndSend(topic, paymentEventDto);
        log.info("Send message to topic '{}': {}", topic, paymentEventDto);
    }
}
