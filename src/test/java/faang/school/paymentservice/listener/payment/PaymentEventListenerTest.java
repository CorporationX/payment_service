package faang.school.paymentservice.listener.payment;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.paymentservice.event.payment.PaymentEvent;
import faang.school.paymentservice.exception.DataValidationException;
import faang.school.paymentservice.model.PaymentStatus;
import faang.school.paymentservice.service.payment.PaymentStatusService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.connection.Message;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentEventListenerTest {

    @Mock
    private PaymentStatusService paymentStatusUpdater;

    @Mock
    private Message message;

    @InjectMocks
    private PaymentEventListener paymentEventListener;

    private ObjectMapper objectMapper = new ObjectMapper();
    private PaymentEvent paymentEvent;

    @BeforeEach
    void setUp() {
        UUID paymentId = UUID.randomUUID();
        paymentEvent = PaymentEvent.builder()
                .id(paymentId)
                .status(PaymentStatus.CONFIRM_PENDING)
                .build();

        paymentEventListener = new PaymentEventListener(objectMapper, paymentStatusUpdater);
    }

    @Test
    @DisplayName("Handles PaymentEvent message and updates payment status")
    void onMessageHandlesPaymentEventAndUpdatesStatus() throws Exception {
        String messageContent = objectMapper.writeValueAsString(paymentEvent);
        when(message.getBody()).thenReturn(messageContent.getBytes(StandardCharsets.UTF_8));

        paymentEventListener.onMessage(message, null);

        verify(paymentStatusUpdater).updatePaymentStatusById(paymentEvent.getId(), paymentEvent.getStatus());
    }

    @Test
    @DisplayName("Logs an error and throws RuntimeException if message processing fails")
    void onMessageLogsErrorIfProcessingFails() {
        when(message.getBody()).thenReturn("invalid message".getBytes(StandardCharsets.UTF_8));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> paymentEventListener.onMessage(message, null)
        );

        assertNotNull(exception.getMessage());
        verify(paymentStatusUpdater, never()).updatePaymentStatusById(any(), any());
    }
}
