package faang.school.paymentservice.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.paymentservice.config.kafka.KafkaProperties;
import faang.school.paymentservice.dto.PendingDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaOperations;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class PublishEventTest {

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private KafkaProperties properties;

    @InjectMocks
    private PublishEvent publishEvent;

    private PendingDto pendingDto;
    private List<PendingDto> pendingDtoList;
    private String pendingDtoString;
    private String pendingDtoListString;

    @BeforeEach
    public void setUp() throws JsonProcessingException {
        pendingDto = new PendingDto();
        pendingDto.setId(1L);
        pendingDtoList = List.of(pendingDto);
        pendingDtoString = "{\"id\":1}";
        pendingDtoListString = "[{\"id\":1}]";
    }

    @Test
    void publishMessageAuthorization_shouldThrowRuntimeException_whenJsonProcessingExceptionOccurs() throws JsonProcessingException {
        String jsonEvent = "{\"status\":\"SUCCESS\"}";
        when(objectMapper.writeValueAsString(pendingDto)).thenReturn(jsonEvent);
        when(kafkaTemplate.executeInTransaction(any())).thenAnswer(invocation -> {
            KafkaOperations.OperationsCallback<String, String, Boolean> callback = invocation.getArgument(0);
            return callback.doInOperations(kafkaTemplate);
        });

        publishEvent.publishMessageAuthorization(pendingDto);

        verify(kafkaTemplate).executeInTransaction(any());
        verify(kafkaTemplate).send("payment-authorization", jsonEvent);
    }

    @Test
    void publish_shouldThrowExceptionWhenJsonProcessingFails() {
        when(kafkaTemplate.executeInTransaction(any())).thenAnswer(invocation -> {
            KafkaOperations.OperationsCallback<String, String, Boolean> callback = invocation.getArgument(0);
            when(objectMapper.writeValueAsString(pendingDto)).thenThrow(new JsonProcessingException("Ошибка JSON") {});
            return callback.doInOperations(kafkaTemplate);
        });

        assertThrows(RuntimeException.class, () -> publishEvent.publishMessageAuthorization(pendingDto));

        verify(kafkaTemplate).executeInTransaction(any());
        verify(kafkaTemplate, never()).send(anyString(), anyString());
    }

    @Test
    void publishMessageAuthorization_shouldPublishMessageToKafka() {
        publishEvent.publishMessageAuthorization(pendingDto);

        verify(kafkaTemplate).executeInTransaction(any());
    }

    @Test
    void publishMessageClear_shouldPublishMessageToKafka() {
        publishEvent.publishMessageClear(pendingDtoList);

        verify(kafkaTemplate).executeInTransaction(any());
    }
}