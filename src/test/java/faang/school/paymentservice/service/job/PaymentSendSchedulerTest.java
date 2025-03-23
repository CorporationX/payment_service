package faang.school.paymentservice.service.job;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.BaseIntegrationTest;
import faang.school.paymentservice.dto.AuthorizationMessageRequest;
import faang.school.paymentservice.dto.CancelMessageRequest;
import faang.school.paymentservice.dto.ClearingMessageRequest;
import faang.school.paymentservice.entity.Payment;
import faang.school.paymentservice.enums.CancelType;
import faang.school.paymentservice.enums.ClearingType;
import faang.school.paymentservice.repository.PaymentHashRepository;
import faang.school.paymentservice.repository.PaymentRepository;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.test.context.jdbc.Sql;

import java.time.Duration;
import java.util.List;
import java.util.Properties;
import java.util.UUID;
import java.util.stream.StreamSupport;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PaymentSendSchedulerTest extends BaseIntegrationTest {
    private static final String PAYMENT_ID = "550e8400-e29b-41d4-a716-446655440000";
    private static KafkaConsumer<String, Object> kafkaConsumer;
    @Autowired
    private PaymentSendScheduler paymentSendScheduler;
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private PaymentHashRepository paymentHashRepository;
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeAll
    static void init() {
        Properties props = new Properties();

        props.put("bootstrap.servers", KAFKA_CONTAINER.getBootstrapServers());
        props.put("group.id", "test-group");
        props.put("key.deserializer", StringDeserializer.class.getName());
        props.put("value.deserializer", JsonDeserializer.class.getName());
        props.put("auto.offset.reset", "earliest");
        props.put("spring.json.trusted.packages", "faang.school.paymentservice.dto");
        kafkaConsumer = new KafkaConsumer<>(props);
        kafkaConsumer.subscribe(List.of("auth-message-request-topic",
                "cancel-message-request-topic",
                "clearing-message-request-topic"));
    }

    @AfterEach
    void tearDown() {
        paymentHashRepository.deleteAll();
    }

    @Sql(scripts = "/clear-payments.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Sql(scripts = "/insert-new-payment.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Test
    void sendAuthorizeNewPayments() throws InterruptedException {
        Payment payment = paymentRepository.findById(UUID.fromString(PAYMENT_ID)).get();
        paymentSendScheduler.sendAuthorizeNewPayments();

        List<ConsumerRecord<String, Object>> records = StreamSupport.stream(kafkaConsumer.poll(Duration.ofSeconds(10))
                        .records("auth-message-request-topic").spliterator(), false)
                .toList();

        assertEquals(1, records.size());
        AuthorizationMessageRequest request = objectMapper.convertValue(records.get(0).value(), AuthorizationMessageRequest.class);
        assertEquals(request.paymentType(), payment.getPaymentType());
        assertEquals(0, request.amount().compareTo(payment.getAmount()));
        assertEquals(request.currency(), payment.getCurrency());
        assertEquals(request.receiverAccountNumber(), payment.getReceiverAccountNumber());
        assertEquals(request.senderAccountNumber(), payment.getSenderAccountNumber());
    }

    @Sql(scripts = "/clear-payments.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Sql(scripts = "/insert-cancellation-payment.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Test
    void sendCancelPayments() {
        Payment payment = paymentRepository.findById(UUID.fromString(PAYMENT_ID)).get();
        paymentSendScheduler.sendCancelPayments();

        List<ConsumerRecord<String, Object>> records = StreamSupport.stream(kafkaConsumer.poll(Duration.ofSeconds(10))
                        .records("cancel-message-request-topic").spliterator(), false)
                .toList();
        assertEquals(1, records.size());
        CancelMessageRequest request = objectMapper.convertValue(records.get(0).value(), CancelMessageRequest.class);
        assertEquals(request.paymentId(), payment.getId());
        assertEquals(CancelType.CANCEL_BY_USER, request.cancelType());
    }

    @Sql(scripts = "/clear-payments.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Sql(scripts = "/insert-clearing-payment.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Test
    void sendClearingPayments() {
        Payment payment = paymentRepository.findById(UUID.fromString(PAYMENT_ID)).get();
        paymentSendScheduler.sendClearingPayments();

        List<ConsumerRecord<String, Object>> records = StreamSupport.stream(kafkaConsumer.poll(Duration.ofSeconds(10))
                        .records("clearing-message-request-topic").spliterator(), false)
                .toList();
        assertEquals(1, records.size());
        ClearingMessageRequest request = objectMapper.convertValue(records.get(0).value(), ClearingMessageRequest.class);
        assertEquals(request.paymentId(), payment.getId());
        assertEquals(ClearingType.SCHEDULER_CLEARING, request.clearingType());
    }
}