package faang.school.paymentservice.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.paymentservice.dto.payment.PaymentEventDto;
import faang.school.paymentservice.model.PaymentStatus;
import faang.school.paymentservice.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentEventResponceListener implements MessageListener {
    private final ObjectMapper objectMapper;
    private final PaymentService paymentService;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String messageBody = new String(message.getBody(), StandardCharsets.UTF_8);
        log.info("Get message: {}", messageBody);
        try {
            PaymentEventDto paymentEventDto = objectMapper.readValue(messageBody, PaymentEventDto.class);
            UUID paymentId = paymentEventDto.getPaymentId();
            PaymentStatus status = paymentEventDto.getStatus();
            paymentService.updatePaymentStatusFromResponce(paymentId, status);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }
}
