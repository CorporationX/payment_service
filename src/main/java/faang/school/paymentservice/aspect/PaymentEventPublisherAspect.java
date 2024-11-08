package faang.school.paymentservice.aspect;

import faang.school.paymentservice.config.redis.RedisProperties;
import faang.school.paymentservice.model.Payment;
import faang.school.paymentservice.publisher.payment.PaymentEventPublisher;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class PaymentEventPublisherAspect {
    private final PaymentEventPublisher publisher;
    private final RedisProperties properties;

    @AfterReturning(pointcut = "@annotation(PublishPaymentEvent)", returning = "payment")
    public void afterReturningAdvice(Payment payment) {
        String topic = switch (payment.getStatus()) {
            case AUTH_PENDING -> properties.getAuthTopic();
            case SCHEDULED_PENDING -> properties.getScheduledTopic();
            case CANCEL_PENDING -> properties.getCancelTopic();
            case FORCED_PENDING -> properties.getForcedTopic();
            default -> throw new IllegalArgumentException("Invalid payment status");
        };

        publisher.publish(topic, payment);
    }
}
