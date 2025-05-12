package faang.school.paymentservice.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.paymentservice.config.kafka.Topics;
import faang.school.paymentservice.dto.PaymentStatus;
import faang.school.paymentservice.dto.message.ClearingMessage;
import faang.school.paymentservice.dto.message.KafkaMessageWrapper;
import faang.school.paymentservice.handler.message.ClearingMessageHandler;
import faang.school.paymentservice.mapper.PaymentMapper;
import faang.school.paymentservice.model.OutboxEvent;
import faang.school.paymentservice.model.PaymentOperation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ClearingMessageHandlerTest {
    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private PaymentMapper paymentMapper;

    @Mock
    private Topics topics;

    @InjectMocks
    private ClearingMessageHandler clearingMessageHandler;

    private OutboxEvent testEvent;
    private PaymentOperation testOperation;
    private ClearingMessage testMessage;
    private final String testTopic = "clearing-topic";
    private final String testPayload = "{\"id\":\"123\",\"authorizationId\":\"456\"}";

    @BeforeEach
    void setUp() {
        testEvent = new OutboxEvent();
        testEvent.setPayload(testPayload);

        testOperation = new PaymentOperation();
        testOperation.setId(UUID.randomUUID());
        testOperation.setAuthorizationId(UUID.randomUUID());

        testMessage = new ClearingMessage();
        testMessage.setOperationId(testOperation.getId());
        testMessage.setAuthorizationId(testOperation.getAuthorizationId());
        testMessage.setTimestamp(Instant.now());
    }

    @Test
    void givenClearedStatus_whenCanHandle_thenReturnTrue() {
        assertTrue(clearingMessageHandler.canHandle(PaymentStatus.CLEARED));
    }

    @Test
    void givenNonClearedStatus_whenCanHandle_thenReturnFalse() {
        assertFalse(clearingMessageHandler.canHandle(PaymentStatus.PENDING));
        assertFalse(clearingMessageHandler.canHandle(PaymentStatus.AUTHORIZED));
        assertFalse(clearingMessageHandler.canHandle(PaymentStatus.CANCELED));
    }

    @Test
    void givenValidPayload_whenHandle_thenReturnCorrectMessage() throws JsonProcessingException {
        when(objectMapper.readValue(testPayload, PaymentOperation.class)).thenReturn(testOperation);
        when(paymentMapper.toClearingMessage(testOperation)).thenReturn(testMessage);
        when(topics.getClearingTopic()).thenReturn(testTopic);

        KafkaMessageWrapper result = clearingMessageHandler.handle(testEvent);

        assertNotNull(result);
        assertEquals(testMessage, result.getPaymentOperationMessage());
        assertEquals(testTopic, result.getTopic());

        ClearingMessage message = (ClearingMessage) result.getPaymentOperationMessage();
        assertNotNull(message.getOperationId());
        assertNotNull(message.getAuthorizationId());
        assertNotNull(message.getTimestamp());
    }

    @Test
    void givenInvalidPayload_whenHandle_thenThrowJsonProcessingException() throws JsonProcessingException {
        when(objectMapper.readValue(testPayload, PaymentOperation.class))
                .thenThrow(new JsonProcessingException("Parse error") {});

        assertThrows(JsonProcessingException.class, () ->
                clearingMessageHandler.handle(testEvent));

        verify(objectMapper).readValue(testPayload, PaymentOperation.class);
        verifyNoInteractions(paymentMapper);
        verifyNoInteractions(topics);
    }

    @Test
    void givenNullEvent_whenHandle_thenThrowNullPointerException() {
        assertThrows(NullPointerException.class, () ->
                clearingMessageHandler.handle(null));
    }

    @Test
    void givenInvalidPaymentOperation_whenHandle_thenThrowIllegalArgumentException() throws JsonProcessingException {
        PaymentOperation invalidOperation = new PaymentOperation();
        invalidOperation.setId(null);
        invalidOperation.setAuthorizationId(null);

        when(objectMapper.readValue(testPayload, PaymentOperation.class)).thenReturn(invalidOperation);
        when(paymentMapper.toClearingMessage(invalidOperation))
                .thenThrow(new IllegalArgumentException("Invalid payment operation"));

        assertThrows(IllegalArgumentException.class, () ->
                clearingMessageHandler.handle(testEvent));
    }

    @Test
    void givenValidOperation_whenHandle_thenVerifyTopicCorrect() throws JsonProcessingException {
        when(objectMapper.readValue(testPayload, PaymentOperation.class)).thenReturn(testOperation);
        when(paymentMapper.toClearingMessage(testOperation)).thenReturn(testMessage);
        when(topics.getClearingTopic()).thenReturn(testTopic);

        KafkaMessageWrapper result = clearingMessageHandler.handle(testEvent);

        verify(topics).getClearingTopic();
        assertEquals(testTopic, result.getTopic());
    }
}