package faang.school.paymentservice.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.paymentservice.dto.event.PaymentCancelEvent;
import faang.school.paymentservice.service.payment.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

@RequiredArgsConstructor
@Component
public class PaymentCancelEventListener implements MessageListener {
    private final PaymentService paymentService;
    private final ObjectMapper objectMapper;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        PaymentCancelEvent event;
        try {
            event = objectMapper.readValue(message.getBody(), PaymentCancelEvent.class);
            paymentService.cancelPayment(event);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
