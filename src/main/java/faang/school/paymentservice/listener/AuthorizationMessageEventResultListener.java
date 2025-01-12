package faang.school.paymentservice.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.paymentservice.event.AuthorizationMessageEvent;
import faang.school.paymentservice.event.AuthorizationMessageResultEvent;
import faang.school.paymentservice.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthorizationMessageEventResultListener implements MessageListener {
    private final ObjectMapper objectMapper;

    private final PaymentService paymentService;
    @Override
    public void onMessage(Message message, byte[] pattern) {
        AuthorizationMessageResultEvent authorization = readMessage(message);


    }

    public AuthorizationMessageResultEvent readMessage(Message message) {
        String stringMessage = new String(message.getBody());

        try {
            return objectMapper.readValue(stringMessage, AuthorizationMessageResultEvent.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
