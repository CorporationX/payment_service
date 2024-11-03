package faang.school.paymentservice.publisher.payment;

import faang.school.paymentservice.model.Payment;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class PaymentEventPublisherAspect {
    private final PaymentEventPublisher publisher;

    @AfterReturning(pointcut = "@annotation(PublishPaymentEvent)", returning = "payment")
    public void afterReturningAdvice(Payment payment) {
        publisher.publish(payment);
    }
}
