package faang.school.paymentservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.paymentservice.dto.outbox.OutboxStatus;
import faang.school.paymentservice.event.PaymentEvent;
import faang.school.paymentservice.mapper.OutboxMapper;
import faang.school.paymentservice.model.OutboxEvent;
import faang.school.paymentservice.model.PaymentOperation;
import faang.school.paymentservice.repository.OutboxEventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PaymentOutboxServiceTest {

    @Mock
    private OutboxEventRepository outboxRepository;

    @Mock
    private OutboxMapper outboxMapper;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private PaymentOutboxService paymentOutboxService;

    private PaymentEvent paymentEvent;
    private PaymentOperation paymentOperation;
    private OutboxEvent outboxEvent;

    @BeforeEach
    void setUp() {
        paymentOperation = new PaymentOperation();
        paymentOperation.setId(UUID.randomUUID());
        paymentEvent = new PaymentEvent(paymentOperation);
        outboxEvent = new OutboxEvent();
    }

    @Test
    void givenPaymentEvent_whenHandleOutboxEvent_thenSuccess() throws JsonProcessingException {
        String jsonPayload = "{\"id\":\"" + paymentOperation.getId() + "\"}";
        when(outboxMapper.toOutboxEvent(paymentOperation)).thenReturn(outboxEvent);
        when(objectMapper.writeValueAsString(paymentOperation)).thenReturn(jsonPayload);

        paymentOutboxService.handleOutboxEvent(paymentEvent);

        verify(outboxMapper).toOutboxEvent(paymentOperation);
        verify(objectMapper).writeValueAsString(paymentOperation);
        verify(outboxRepository).save(outboxEvent);

        assertEquals(jsonPayload, outboxEvent.getPayload());
        assertEquals(OutboxStatus.NEW, outboxEvent.getOutboxStatus());
    }

    @Test
    void givenPaymentEvent_whenHandleOutboxEvent_thenFailed() throws JsonProcessingException {
        JsonProcessingException exception = new JsonProcessingException("Serialization error") {};
        when(outboxMapper.toOutboxEvent(paymentOperation)).thenReturn(outboxEvent);
        when(objectMapper.writeValueAsString(paymentOperation)).thenThrow(exception);

        paymentOutboxService.handleOutboxEvent(paymentEvent);

        verify(outboxRepository).save(outboxEvent);
    }

}
