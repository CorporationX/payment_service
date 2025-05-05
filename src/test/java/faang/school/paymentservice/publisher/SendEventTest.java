package faang.school.paymentservice.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import faang.school.paymentservice.dto.PaymentStatus;
import faang.school.paymentservice.dto.message.KafkaMessageWrapper;
import faang.school.paymentservice.dto.message.PaymentOperationMessage;
import faang.school.paymentservice.dto.outbox.OutboxStatus;
import faang.school.paymentservice.exception.NoHandlerFoundException;
import faang.school.paymentservice.handler.message.EventHandler;
import faang.school.paymentservice.model.OutboxEvent;
import faang.school.paymentservice.repository.OutboxEventRepository;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.TopicPartition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SendEventTest {

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Mock
    private EventHandler handler1;

    @Mock
    private EventHandler handler2;

    @Mock
    private OutboxEventRepository repository;

    @InjectMocks
    private SendEvent sendEvent;

    private OutboxEvent testEvent;
    private KafkaMessageWrapper testMessage;
    private PaymentOperationMessage paymentOperationMessage;

    @BeforeEach
    void setUp() {
        testEvent = new OutboxEvent();
        testEvent.setId(UUID.randomUUID());
        testEvent.setEventType(PaymentStatus.PENDING);
        testEvent.setOutboxStatus(OutboxStatus.NEW);

        testMessage = new KafkaMessageWrapper(paymentOperationMessage, "payment-topic");

        sendEvent = new SendEvent(kafkaTemplate, List.of(handler1, handler2), repository);
    }

    @Test
    void sendEventToKafka_ShouldProcessSuccessfully_WhenHandlerExists() throws JsonProcessingException {
        when(handler1.canHandle(PaymentStatus.PENDING)).thenReturn(true);
        when(handler1.handle(testEvent)).thenReturn(testMessage);

        CompletableFuture<SendResult<String, Object>> future = CompletableFuture.completedFuture(
                createSuccessSendResult("payment-topic"));
        when(kafkaTemplate.send("payment-topic", paymentOperationMessage)).thenReturn(future);

        sendEvent.sendEventToKafka(testEvent);

        verify(handler1).handle(testEvent);
        verify(repository).save(testEvent);
        assertEquals(OutboxStatus.SENT, testEvent.getOutboxStatus());
        assertNotNull(testEvent.getSentAt());
    }

    @Test
    void givenInvalidData_whenSendEventToKafka_thenJsonProcessingFails() throws JsonProcessingException {
        when(handler1.canHandle(PaymentStatus.PENDING)).thenReturn(true);
        when(handler1.handle(testEvent)).thenThrow(new JsonProcessingException("Error") {
        });

        sendEvent.sendEventToKafka(testEvent);

        verify(repository).save(testEvent);
        assertEquals(OutboxStatus.ERROR, testEvent.getOutboxStatus());
        assertNull(testEvent.getSentAt());
    }

    @Test
    void givenInvalidData_whenSendEventToKafka_thenThrowException() {
        when(handler1.canHandle(PaymentStatus.PENDING)).thenReturn(false);
        when(handler2.canHandle(PaymentStatus.PENDING)).thenReturn(false);

        assertThrows(NoHandlerFoundException.class, () -> sendEvent.sendEventToKafka(testEvent));
    }

    private SendResult<String, Object> createSuccessSendResult(String topic) {
        TopicPartition topicPartition = new TopicPartition(topic, 0);
        long timestamp = System.currentTimeMillis();
        int keySize = 0;
        int valueSize = 0;

        RecordMetadata metadata = new RecordMetadata(
                topicPartition,
                -1L,
                -1,
                timestamp,
                keySize,
                valueSize
        );

        return new SendResult<>(null, metadata);
    }
}
