package faang.school.paymentservice.publisher.payment;

import faang.school.paymentservice.event.payment.PaymentEvent;
import faang.school.paymentservice.model.Payment;
import faang.school.paymentservice.publisher.MessagePublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentEventPublisher implements MessagePublisher<PaymentEvent> {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic paymentAuthPendingTopic;
    private final ChannelTopic paymentConfirmPendingTopic;
    private final ChannelTopic paymentCancelPendingTopic;
    private final ChannelTopic paymentClearPendingTopic;

    @Override
    public void publish(PaymentEvent message, String topic) {
        redisTemplate.convertAndSend(topic, message);
        log.info("Message was send {}, in topic - {}", message, topic);
    }

    public void publishPayment(Payment payment) {
        PaymentEvent event = PaymentEvent.builder()
                .id(payment.getId())
                .sourceAccountId(payment.getSourceAccountId())
                .targetAccountId(payment.getTargetAccountId())
                .amount(payment.getAmount())
                .currency(payment.getCurrency())
                .status(payment.getStatus())
                .category(payment.getCategory())
                .clearScheduledAt(payment.getClearScheduledAt())
                .build();

        ChannelTopic topic = switch (payment.getStatus()) {
            case AUTH_PENDING -> paymentAuthPendingTopic;
            case CONFIRM_PENDING -> paymentConfirmPendingTopic;
            case CANCEL_PENDING -> paymentCancelPendingTopic;
            case CLEAR_PENDING -> paymentClearPendingTopic;
            default -> throw new IllegalArgumentException("Unsupported payment status");
        };

        publish(event, topic.getTopic());
    }
}
