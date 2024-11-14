package faang.school.paymentservice.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.paymentservice.dto.event.PaymentApproveEvent;
import faang.school.paymentservice.service.payment.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

@RequiredArgsConstructor
@Component
@Slf4j
public class PaymentApproveEventListener implements MessageListener {
    private final ObjectMapper objectMapper;
    private final PaymentService paymentService;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        log.info("New message received: {}", message);
        PaymentApproveEvent event;
        try {
            event = objectMapper.readValue(message.getBody(), PaymentApproveEvent.class);
            paymentService.approvePayment(event);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
