package faang.school.paymentservice.controller;

import faang.school.BaseIntegrationTest;
import faang.school.paymentservice.dto.PaymentRequest;
import faang.school.paymentservice.dto.PaymentResponse;
import faang.school.paymentservice.entity.Payment;
import faang.school.paymentservice.enums.Currency;
import faang.school.paymentservice.enums.PaymentStatus;
import faang.school.paymentservice.enums.PaymentType;
import faang.school.paymentservice.repository.PaymentHashRepository;
import faang.school.paymentservice.repository.PaymentRepository;
import faang.school.paymentservice.service.PaymentService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PaymentControllerTest extends BaseIntegrationTest {
    private PaymentRequest paymentRequest;
    @Autowired
    private PaymentService paymentService;
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private PaymentHashRepository paymentHashRepository;

    @BeforeEach
    void setUp() {
        paymentRequest = PaymentRequest.builder()
                .paymentType(PaymentType.EDUCATION)
                .amount(BigDecimal.valueOf(100))
                .currency(Currency.RUB)
                .receiverAccountNumber("000000000001")
                .senderAccountNumber("000000000002")
                .build();
    }

    @AfterEach
    void tearDown() {
        paymentRepository.deleteAll();
        paymentHashRepository.deleteAll();
    }

    @Test
    void sendPayment() {
        PaymentResponse paymentResponse = webTestClient.post()
                .uri("/api/payment/send")
                .bodyValue(paymentRequest)
                .exchange()
                .expectStatus().isOk()
                .expectBody(PaymentResponse.class)
                .returnResult()
                .getResponseBody();


        webTestClient.get()
                .uri("/api/payment?paymentId=" + paymentResponse.id())
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(paymentResponse.id().toString())
                .jsonPath("$.senderAccountNumber").isEqualTo(paymentResponse.senderAccountNumber())
                .jsonPath("$.receiverAccountNumber").isEqualTo(paymentResponse.receiverAccountNumber())
                .jsonPath("$.amount").isEqualTo(paymentResponse.amount().doubleValue())
                .jsonPath("$.currency").isEqualTo(paymentResponse.currency().name())
                .jsonPath("$.paymentType").isEqualTo(paymentResponse.paymentType().name())
                .jsonPath("$.paymentDateTime[0]").isEqualTo(paymentResponse.paymentDateTime().getYear())
                .jsonPath("$.paymentDateTime[1]").isEqualTo(paymentResponse.paymentDateTime().getMonthValue())
                .jsonPath("$.paymentDateTime[2]").isEqualTo(paymentResponse.paymentDateTime().getDayOfMonth())
                .jsonPath("$.paymentStatus").isEqualTo(paymentResponse.paymentStatus().name());
    }

    @Test
    void sendDuplicate() {
        webTestClient.post()
                .uri("/api/payment/send")
                .bodyValue(paymentRequest)
                .exchange()
                .expectStatus().isOk()
                .expectBody(PaymentResponse.class)
                .returnResult()
                .getResponseBody();

        webTestClient.post()
                .uri("/api/payment/send")
                .bodyValue(paymentRequest)
                .exchange()
                .expectStatus().isBadRequest();

    }

    @Test
    void cancelPayment() {
        Payment payment = getPaymentWithStatus("a0579dd6-31f1-334a-b313-75205f88e5cb",
                PaymentStatus.AUTHORIZED);

        PaymentResponse paymentResponse = webTestClient.post()
                .uri("/api/payment/cancel?paymentId=" + payment.getId())
                .exchange()
                .expectStatus().isOk()
                .expectBody(PaymentResponse.class)
                .returnResult()
                .getResponseBody();

        webTestClient.get()
                .uri("/api/payment?paymentId=" + paymentResponse.id())
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(paymentResponse.id().toString())
                .jsonPath("$.paymentStatus").isEqualTo(PaymentStatus.PROCESS_OF_CANCELLATION.name());
    }

    @Test
    void cancelPaymentNotFound() {
        Payment payment = getPaymentWithStatus("a0579dd6-31f1-334a-b313-75205f88e5cb",
                PaymentStatus.AUTHORIZED);

        webTestClient.post()
                .uri("/api/payment/cancel?paymentId=" + payment.getId())
                .exchange()
                .expectStatus().isOk()
                .expectBody(PaymentResponse.class)
                .returnResult()
                .getResponseBody();

        webTestClient.get()
                .uri("/api/payment?paymentId=" + "b0579dd6-31f1-334a-b313-75205f88e5cb")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void clearingPayment() {
        Payment payment = getPaymentWithStatus("a0579dd6-31f1-334a-b313-75205f88e5ca",
                PaymentStatus.AUTHORIZED);

        PaymentResponse paymentResponse = webTestClient.post()
                .uri("/api/payment/clearing?paymentId=" + payment.getId())
                .exchange()
                .expectStatus().isOk()
                .expectBody(PaymentResponse.class)
                .returnResult()
                .getResponseBody();

        webTestClient.get()
                .uri("/api/payment?paymentId=" + paymentResponse.id())
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(paymentResponse.id().toString())
                .jsonPath("$.paymentStatus").isEqualTo(PaymentStatus.PROCESS_OF_CLEARING.name());
    }

    private Payment getPaymentWithStatus(String paymentId, PaymentStatus paymentStatus) {
        Payment payment = Payment.builder()
                .id(UUID.fromString(paymentId))
                .paymentType(PaymentType.EDUCATION)
                .amount(BigDecimal.valueOf(100))
                .currency(Currency.RUB)
                .receiverAccountNumber("000000000001")
                .senderAccountNumber("000000000002")
                .paymentDateTime(LocalDateTime.now().plusDays(1))
                .createdAt(LocalDateTime.now().minusMinutes(10))
                .paymentStatus(paymentStatus)
                .build();
        paymentService.savePayment(payment);
        return payment;
    }
}