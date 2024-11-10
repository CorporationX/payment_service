package faang.school.paymentservice.listener.payment;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.paymentservice.event.payment.PaymentEvent;
import faang.school.paymentservice.listener.AbstractEventListener;
import faang.school.paymentservice.service.payment.PaymentStatusService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PaymentEventListener extends AbstractEventListener<PaymentEvent> {

    private final PaymentStatusService paymentStatusUpdater;

    public PaymentEventListener(ObjectMapper objectMapper, PaymentStatusService paymentStatusUpdater) {
        super(objectMapper);

        this.paymentStatusUpdater = paymentStatusUpdater;
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        log.info("Got message, trying to handle it");
        handleEvent(message, PaymentEvent.class, event -> {
            paymentStatusUpdater.updatePaymentStatusById(event.getId(), event.getStatus());
        });
    }
}
