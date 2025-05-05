package faang.school.paymentservice.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.paymentservice.config.kafka.Topics;
import faang.school.paymentservice.dto.PaymentStatus;
import faang.school.paymentservice.dto.message.CancellationMessage;
import faang.school.paymentservice.dto.message.KafkaMessageWrapper;
import faang.school.paymentservice.handler.message.CancellationMessageHandler;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CancellationMessageHandlerTest {
    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private PaymentMapper paymentMapper;

    @Mock
    private Topics topics;

    @InjectMocks
    private CancellationMessageHandler cancellationMessageHandler;

    private OutboxEvent testEvent;
    private PaymentOperation testOperation;
    private CancellationMessage testMessage;
    private final String testTopic = "cancellation-topic";
    private final String testPayload = "{\"id\":\"123\",\"authorizationId\":\"456\"}";

    @BeforeEach
    void setUp() {
        testEvent = new OutboxEvent();
        testEvent.setPayload(testPayload);

        testOperation = new PaymentOperation();
        testOperation.setId(UUID.randomUUID());
        testOperation.setAuthorizationId(UUID.randomUUID());

        testMessage = new CancellationMessage();
        testMessage.setOperationId(testOperation.getId());
        testMessage.setAuthorizationId(testOperation.getAuthorizationId());
        testMessage.setTimestamp(Instant.now());
    }

    @Test
    void givenValidPaymentStatus_whenCanHandle_thenReturnTrue() {
        assertTrue(cancellationMessageHandler.canHandle(PaymentStatus.CANCELED));
    }

    @Test
    void givenInvalidPaymentStatus_whenCanHandle_thenReturnFalse() {
        assertFalse(cancellationMessageHandler.canHandle(PaymentStatus.PENDING));
        assertFalse(cancellationMessageHandler.canHandle(PaymentStatus.AUTHORIZED));
        assertFalse(cancellationMessageHandler.canHandle(PaymentStatus.CLEARED));
    }

    @Test
    void givenValidPayload_whenHandle_thenCorrectMessage() throws JsonProcessingException {
        when(objectMapper.readValue(testPayload, PaymentOperation.class)).thenReturn(testOperation);
        when(paymentMapper.toCancellationMessage(testOperation)).thenReturn(testMessage);
        when(topics.getCancellationTopic()).thenReturn(testTopic);

        KafkaMessageWrapper result = cancellationMessageHandler.handle(testEvent);

        assertNotNull(result);
        assertEquals(testMessage, result.getPaymentOperationMessage());
        assertEquals(testTopic, result.getTopic());

        CancellationMessage message = (CancellationMessage) result.getPaymentOperationMessage();
        assertNotNull(message.getOperationId());
        assertNotNull(message.getAuthorizationId());
        assertNotNull(message.getTimestamp());
    }

    @Test
    void givenInvalidPayload_whenHandle_thenException() throws JsonProcessingException {
        when(objectMapper.readValue(testPayload, PaymentOperation.class))
                .thenThrow(new JsonProcessingException("Parse error") {
                });

        assertThrows(JsonProcessingException.class, () ->
                cancellationMessageHandler.handle(testEvent));

        verify(objectMapper).readValue(testPayload, PaymentOperation.class);
        verifyNoInteractions(paymentMapper);
        verifyNoInteractions(topics);
    }

    @Test
    void givenNullEvent_whenHandle_thenThrowNullPointerException() {
        assertThrows(NullPointerException.class, () ->
                cancellationMessageHandler.handle(null));
    }

    @Test
    void givenInvalidPaymentOperation_whenHandle_thenThrowException() throws JsonProcessingException {
        PaymentOperation invalidOperation = new PaymentOperation();
        invalidOperation.setId(null);
        invalidOperation.setAuthorizationId(null);

        when(objectMapper.readValue(testPayload, PaymentOperation.class)).thenReturn(invalidOperation);
        when(paymentMapper.toCancellationMessage(invalidOperation))
                .thenThrow(new IllegalArgumentException("Invalid payment operation"));

        assertThrows(IllegalArgumentException.class, () ->
                cancellationMessageHandler.handle(testEvent));
    }
}