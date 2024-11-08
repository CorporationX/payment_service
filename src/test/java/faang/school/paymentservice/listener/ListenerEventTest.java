package faang.school.paymentservice.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.paymentservice.dto.PendingDto;
import faang.school.paymentservice.service.PendingServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.support.Acknowledgment;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ListenerEventTest {

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private PendingServiceImpl pendingService;

    @Mock
    private Acknowledgment acknowledgment;

    @InjectMocks
    private ListenerEvent listenerEvent;

    private PendingDto pendingDto;
    private String messageJson;

    @BeforeEach
    void setUp() {
        pendingDto = new PendingDto();
        pendingDto.setId(1L);
        messageJson = "{\"id\":1}"; // Пример сообщения в формате JSON
    }

    @Test
    void listenMessagePendingDto_shouldProcessMessageSuccessfully() throws JsonProcessingException {
        when(objectMapper.readValue(messageJson, PendingDto.class)).thenReturn(pendingDto);
        listenerEvent.listenMessagePendingDto(messageJson, acknowledgment);

        verify(objectMapper).readValue(messageJson, PendingDto.class);
        verify(pendingService).resetStatus(pendingDto);
        verify(acknowledgment).acknowledge();
    }

    @Test
    void listenMessagePendingDto_shouldThrowExceptionOnJsonProcessingException() throws JsonProcessingException {
        when(objectMapper.readValue(messageJson, PendingDto.class)).thenThrow(new JsonProcessingException("Error") {});

        assertThrows(RuntimeException.class, () -> listenerEvent.listenMessagePendingDto(messageJson, acknowledgment));
        verify(acknowledgment, never()).acknowledge();
    }

}