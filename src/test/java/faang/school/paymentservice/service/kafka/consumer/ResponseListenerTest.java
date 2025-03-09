package faang.school.paymentservice.service.kafka.consumer;

import faang.school.BaseIntegrationTest;
import faang.school.paymentservice.dto.MessageResponse;
import faang.school.paymentservice.entity.Payment;
import faang.school.paymentservice.enums.PaymentStatus;
import faang.school.paymentservice.enums.TransferStatus;
import faang.school.paymentservice.service.PaymentService;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.jdbc.Sql;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ResponseListenerTest extends BaseIntegrationTest {
    private static final String PAYMENT_ID = "550e8400-e29b-41d4-a716-446655440000";
    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    private PaymentService paymentService;

    @Sql(scripts = "/clear-payments.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Sql(scripts = "/insert-new-payment.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Test
    void testAuthResponseListener() {
        MessageResponse response = MessageResponse.builder()
                .paymentId(UUID.fromString(PAYMENT_ID))
                .result(TransferStatus.AUTHORIZED)
                .build();

        String topic = "auth-message-response-topic";
        ProducerRecord<String, Object> record = new ProducerRecord<>(topic, response);
        kafkaTemplate.send(record).join();
        Awaitility.await().atMost(5, TimeUnit.SECONDS).until(() ->
                paymentService.getPaymentById(UUID.fromString(PAYMENT_ID)).getPaymentStatus()
                        .equals(PaymentStatus.AUTHORIZED));
        Payment payment = paymentService.getPaymentById(UUID.fromString(PAYMENT_ID));
        Assertions.assertNotNull(payment);
        assertEquals(PaymentStatus.AUTHORIZED, payment.getPaymentStatus());
    }

    @Sql(scripts = "/clear-payments.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Sql(scripts = "/insert-new-payment.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Test
    void testCancelResponseListener() {
        MessageResponse response = MessageResponse.builder()
                .paymentId(UUID.fromString(PAYMENT_ID))
                .result(TransferStatus.CANCELLED)
                .build();

        String topic = "cancel-message-response-topic";
        ProducerRecord<String, Object> record = new ProducerRecord<>(topic, response);
        kafkaTemplate.send(record).join();
        Awaitility.await().atMost(5, TimeUnit.SECONDS).until(() ->
                paymentService.getPaymentById(UUID.fromString(PAYMENT_ID)).getPaymentStatus()
                        .equals(PaymentStatus.CANCELLED));
        Payment payment = paymentService.getPaymentById(UUID.fromString(PAYMENT_ID));
        Assertions.assertNotNull(payment);
        assertEquals(PaymentStatus.CANCELLED, payment.getPaymentStatus());
    }

    @Sql(scripts = "/clear-payments.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Sql(scripts = "/insert-new-payment.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Test
    void testClearingResponseListener() {
        MessageResponse response = MessageResponse.builder()
                .paymentId(UUID.fromString(PAYMENT_ID))
                .result(TransferStatus.CLEARED)
                .build();

        String topic = "clearing-message-response-topic";
        ProducerRecord<String, Object> record = new ProducerRecord<>(topic, response);
        kafkaTemplate.send(record).join();
        Awaitility.await().atMost(5, TimeUnit.SECONDS).until(() ->
                paymentService.getPaymentById(UUID.fromString(PAYMENT_ID)).getPaymentStatus()
                        .equals(PaymentStatus.CLEARED));
        Payment payment = paymentService.getPaymentById(UUID.fromString(PAYMENT_ID));
        Assertions.assertNotNull(payment);
        assertEquals(PaymentStatus.CLEARED, payment.getPaymentStatus());
    }
}