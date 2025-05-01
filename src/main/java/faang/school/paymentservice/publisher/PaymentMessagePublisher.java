package faang.school.paymentservice.publisher;

import faang.school.paymentservice.dto.outbox.OutboxStatus;
import faang.school.paymentservice.model.OutboxEvent;
import faang.school.paymentservice.repository.OutboxEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PaymentMessagePublisher {
    private final SendEvent sendEvent;
    private final OutboxEventRepository outboxEventRepository;

    @Value("${publisher.batch}")
    private int batch;

    public void publishEvent(OutboxStatus status) {
        Pageable pageable = PageRequest.of(0, batch);
        List<OutboxEvent> newEvents = outboxEventRepository.findByOutboxStatus(status, pageable);

        for (OutboxEvent event : newEvents) {
            sendEvent.sendEventToKafka(event);
        }
    }

}
