package faang.school.paymentservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.paymentservice.event.PaymentEvent;
import faang.school.paymentservice.mapper.OutboxMapper;
import faang.school.paymentservice.model.OutboxEvent;
import faang.school.paymentservice.repository.OutboxEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentOutboxService {
    private final OutboxEventRepository outboxRepository;
    private final OutboxMapper outboxMapper;
    private final ObjectMapper objectMapper;

    @EventListener
    public void handleOutboxEvent(PaymentEvent event) {
        try {
            OutboxEvent outboxEvent = outboxMapper.toOutboxEvent(event.operation());
            outboxEvent.setPayload(objectMapper.writeValueAsString(event.operation()));
            outboxRepository.save(outboxEvent);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
