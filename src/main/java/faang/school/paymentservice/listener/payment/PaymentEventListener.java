package faang.school.paymentservice.listener.payment;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.paymentservice.event.payment.PaymentEvent;
import faang.school.paymentservice.listener.AbstractEventListener;
import faang.school.paymentservice.service.payment.PaymentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PaymentEventListener extends AbstractEventListener<PaymentEvent> {

    private final PaymentService paymentService;

    public PaymentEventListener(ObjectMapper objectMapper, PaymentService paymentService) {
        super(objectMapper);

        this.paymentService = paymentService;
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        log.info("Got message, trying to handle it");
        handleEvent(message, PaymentEvent.class, event -> {
            paymentService.updatePaymentStatusById(event.getId(), event.getStatus());
        });
    }
}
