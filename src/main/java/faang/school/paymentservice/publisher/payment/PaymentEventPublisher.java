package faang.school.paymentservice.publisher.payment;


import faang.school.paymentservice.dto.payment.PaymentEventDto;

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
public class PaymentEventPublisher {
    private final RedisTemplate<String, Object> redisTemplate;
    private final PaymentMapper paymentMapper;

    @Value("${spring.data.redis.channel.payment-event}")
    private String channel;

    public void publish(Payment payment) {
        PaymentEventDto paymentEventDto = paymentMapper.toPaymentEventDto(payment);
        redisTemplate.convertAndSend(channel, paymentEventDto);
        System.out.println(channel);
        log.info("Send message to broker: {}", paymentEventDto);
    }
}
