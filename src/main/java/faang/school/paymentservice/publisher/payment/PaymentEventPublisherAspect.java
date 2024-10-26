package faang.school.paymentservice.publisher.payment;

import faang.school.paymentservice.model.Payment;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class PaymentEventPublisherAspect {
    private final PaymentEventPublisherClass publisher;

    @Pointcut("@annotation(PaymentEventPublisher)")
    public void paymentEventPublishMethods() {}

    @AfterReturning(pointcut = "paymentEventPublishMethods()", returning = "payment")
    public void afterReturningAdvice(Payment payment) {
        publisher.publish(payment);
    }
}
