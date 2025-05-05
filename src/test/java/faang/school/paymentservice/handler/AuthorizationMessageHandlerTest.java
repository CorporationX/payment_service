package faang.school.paymentservice.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.paymentservice.config.kafka.Topics;
import faang.school.paymentservice.dto.Currency;
import faang.school.paymentservice.dto.PaymentStatus;
import faang.school.paymentservice.dto.message.AuthorizationMessage;
import faang.school.paymentservice.dto.message.KafkaMessageWrapper;
import faang.school.paymentservice.handler.message.AuthorizationMessageHandler;
import faang.school.paymentservice.mapper.PaymentMapper;
import faang.school.paymentservice.model.OutboxEvent;
import faang.school.paymentservice.model.PaymentOperation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.UUID;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthorizationMessageHandlerTest {
    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private PaymentMapper paymentMapper;

    @Mock
    private Topics topics;

    @InjectMocks
    private AuthorizationMessageHandler authorizationMessageHandler;

    private OutboxEvent testEvent;
    private PaymentOperation testOperation;
    private AuthorizationMessage testMessage;
    private final String testTopic = "authorization-topic";
    private final String testPayload = "{\"id\":\"123\",\"amount\":100.0,\"currency\":\"USD\"}";

    @BeforeEach
    void setUp() {
        testEvent = new OutboxEvent();
        testEvent.setPayload(testPayload);

        testOperation = new PaymentOperation();
        testOperation.setId(UUID.randomUUID());
        testOperation.setAmount(BigDecimal.valueOf(100.0));
        testOperation.setCurrency(Currency.USD);
        testOperation.setSenderAccountId(UUID.randomUUID());
        testOperation.setRecipientAccountId(UUID.randomUUID());


        testMessage = new AuthorizationMessage();
        testMessage.setOperationId(testOperation.getId());
        testMessage.setSenderAccountId(testOperation.getSenderAccountId());
        testMessage.setRecipientAccountId(testOperation.getRecipientAccountId());
        testMessage.setAmount(testOperation.getAmount());
        testMessage.setCurrency("USD");
    }


    @Test
    void givenValidPaymentStatus_whenCanHandle_thenReturnTrue() {
        assertTrue(authorizationMessageHandler.canHandle(PaymentStatus.PENDING));
    }

    @Test
    void givenInvalidPaymentStatus_whenCanHandle_thenReturnFalse() {
        assertFalse(authorizationMessageHandler.canHandle(PaymentStatus.AUTHORIZED));
        assertFalse(authorizationMessageHandler.canHandle(PaymentStatus.FAILED));
        assertFalse(authorizationMessageHandler.canHandle(PaymentStatus.CLEARED));
    }

    @Test
    void givenValidPayload_whenHandle_thenCorrectMessage() throws JsonProcessingException {
        when(objectMapper.readValue(testPayload, PaymentOperation.class)).thenReturn(testOperation);
        when(paymentMapper.toAuthorizationMessage(testOperation)).thenReturn(testMessage);
        when(topics.getAuthorizationTopic()).thenReturn(testTopic);

        KafkaMessageWrapper result = authorizationMessageHandler.handle(testEvent);

        assertNotNull(result);
        assertEquals(testMessage, result.getPaymentOperationMessage());
        assertEquals(testTopic, result.getTopic());
    }

    @Test
    void givenInvalidPayload_whenHandle_thenException() throws JsonProcessingException {
        when(objectMapper.readValue(testPayload, PaymentOperation.class))
                .thenThrow(new JsonProcessingException("Parse error") {
                });

        assertThrows(JsonProcessingException.class, () -> authorizationMessageHandler.handle(testEvent));

        verify(objectMapper).readValue(testPayload, PaymentOperation.class);
    }
}
