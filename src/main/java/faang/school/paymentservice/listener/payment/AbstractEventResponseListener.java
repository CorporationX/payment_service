package faang.school.paymentservice.listener.payment;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.paymentservice.dto.payment.PaymentEventDto;
import faang.school.paymentservice.model.PaymentStatus;
import faang.school.paymentservice.service.payment.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;

import java.nio.charset.StandardCharsets;
import java.util.Set;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
public abstract class AbstractEventResponseListener implements MessageListener {
    protected final ObjectMapper objectMapper;
    protected final PaymentService paymentService;

    protected abstract Set<PaymentStatus> getAllowedStatuses();

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String messageBody = new String(message.getBody(), StandardCharsets.UTF_8);
        log.info("Get message: {}", messageBody);
        try {
            PaymentEventDto paymentEventDto = objectMapper.readValue(messageBody, PaymentEventDto.class);
            UUID paymentId = paymentEventDto.getPaymentId();
            PaymentStatus status = paymentEventDto.getStatus();

            if (!getAllowedStatuses().contains(status)) {
                throw new IllegalStateException("Incorrect payment response status: " + status);
            }

            paymentService.updatePaymentStatusFromResponce(paymentId, status);
        } catch (JsonProcessingException e) {
            log.error("Failed to process message: {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }
}