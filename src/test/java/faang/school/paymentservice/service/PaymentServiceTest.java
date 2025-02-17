package faang.school.paymentservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.paymentservice.config.KafkaConfig;
import faang.school.paymentservice.dto.PaymentResponse;
import faang.school.paymentservice.dto.PaymentStatus;
import faang.school.paymentservice.dto.PromotionRequest;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.kafka.core.KafkaTemplate;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PaymentServiceTest {

    private ObjectMapper objectMapper = new ObjectMapper();
    private KafkaTemplate<String, String> kafkaTemplate;
    private PaymentService paymentService;

    @BeforeEach
    void setUp() {
        kafkaTemplate = mock(KafkaTemplate.class);
        paymentService = new PaymentService(objectMapper, kafkaTemplate);
    }

    @Test
    void testPaymentPromotionSuccessful() throws Exception {
        PromotionRequest request = PromotionRequest.builder()
                .userId(123L)
                .budgetInDay(100L)
                .countDays(2L)
                .build();
        String jsonRequest = objectMapper.writeValueAsString(request);

        ConsumerRecord<String, String> record =
                new ConsumerRecord<>("dummy", 0, 0L, "key1", jsonRequest);

        paymentService.paymentPromotion(record);

        verify(kafkaTemplate).send(eq(KafkaConfig.PROMOTION_BOUGHT_TOPIC), eq("key1"), eq(jsonRequest));

        ArgumentCaptor<String> topicCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> keyCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        verify(kafkaTemplate, times(2))
                .send(topicCaptor.capture(), keyCaptor.capture(), messageCaptor.capture());

        assertTrue(topicCaptor.getAllValues().contains(KafkaConfig.NOTIFICATION_TOPIC));
        assertTrue(keyCaptor.getAllValues().contains(String.valueOf(request.userId())));

        String jsonResponse = messageCaptor.getAllValues().get(1); // предполагаем, что второй вызов — уведомление
        PaymentResponse response = objectMapper.readValue(jsonResponse, PaymentResponse.class);

        assertEquals(PaymentStatus.SUCCESS, response.status());
        assertEquals(0, BigDecimal.valueOf(200.00).compareTo(response.amount()));
        assertEquals("USD", response.currency().name());
    }

    @Test
    void testDeserializeError() throws Exception {
        String invalidJson = "invalid";
        ConsumerRecord<String, String> record =
                new ConsumerRecord<>("dummy", 0, 0L, "key1", invalidJson);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> paymentService.paymentPromotion(record));
        assertTrue(exception.getMessage().contains("Ошибка десериализации сообщения"));
    }
}
