package faang.school.paymentservice.publisher;

import faang.school.paymentservice.dto.outbox.OutboxStatus;
import faang.school.paymentservice.model.OutboxEvent;
import faang.school.paymentservice.repository.OutboxEventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PaymentMessagePublisherTest {

    @Mock
    private SendEvent sendEvent;

    @Mock
    private OutboxEventRepository outboxEventRepository;

    @InjectMocks
    private PaymentMessagePublisher paymentMessagePublisher;

    private final int testBatchSize = 10;
    private Pageable testPageable;
    private List<OutboxEvent> testEvents;

    @BeforeEach
    public void setUp() {
        testPageable = PageRequest.of(0, testBatchSize);
        OutboxEvent event1 = new OutboxEvent();
        event1.setPayload("payload1");
        event1.setOutboxStatus(OutboxStatus.NEW);

        OutboxEvent event2 = new OutboxEvent();
        event2.setPayload("payload2");
        event2.setOutboxStatus(OutboxStatus.NEW);

        testEvents = List.of(event1, event2);

        ReflectionTestUtils.setField(paymentMessagePublisher, "batch", testBatchSize);
    }

    @Test
    void givenValidData_whenPublishEvent_thenFetchEventsWithGivenStatus() {
        when(outboxEventRepository.findByOutboxStatus(OutboxStatus.NEW, testPageable))
                .thenReturn(testEvents);

        paymentMessagePublisher.publishEvent(OutboxStatus.NEW);

        verify(outboxEventRepository).findByOutboxStatus(OutboxStatus.NEW, testPageable);
    }

    @Test
    void givenValidData_whenPublishEvent_thenSendAllFetchedEventsToKafka() {
        when(outboxEventRepository.findByOutboxStatus(OutboxStatus.NEW, testPageable))
                .thenReturn(testEvents);

        paymentMessagePublisher.publishEvent(OutboxStatus.NEW);

        verify(sendEvent).sendEventToKafka(testEvents.get(0));
        verify(sendEvent).sendEventToKafka(testEvents.get(1));
    }
}
