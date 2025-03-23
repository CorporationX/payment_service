package faang.school.paymentservice.mapper;

import faang.school.paymentservice.dto.PaymentRequest;
import faang.school.paymentservice.dto.PaymentResponse;
import faang.school.paymentservice.entity.Payment;
import faang.school.paymentservice.enums.Currency;
import faang.school.paymentservice.enums.PaymentStatus;
import faang.school.paymentservice.enums.PaymentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class PaymentMapperTest {
    private PaymentMapper paymentMapper;

    @BeforeEach
    void setUp() {
        paymentMapper = new PaymentMapper();
    }

    @Test
    void testPaymentRequestToPayment_WithNullPaymentDateTime() {
        PaymentRequest paymentRequest = PaymentRequest.builder()
                .amount(BigDecimal.valueOf(100.0))
                .receiverAccountNumber("123456789")
                .senderAccountNumber("987654321")
                .currency(Currency.USD)
                .paymentDateTime(null)
                .paymentType(null)
                .build();

        Payment payment = paymentMapper.paymentRequestToPayment(paymentRequest);

        assertNotNull(payment.getPaymentDateTime());
        assertEquals(PaymentType.OTHER, payment.getPaymentType());
        assertEquals(PaymentStatus.NEW, payment.getPaymentStatus());
        assertEquals(BigDecimal.valueOf(100.0), payment.getAmount());
        assertEquals("123456789", payment.getReceiverAccountNumber());
        assertEquals("987654321", payment.getSenderAccountNumber());
        assertEquals(Currency.USD, payment.getCurrency());
    }

    @Test
    void testPaymentRequestToPayment_WithNonNullPaymentDateTime() {
        LocalDateTime now = LocalDateTime.now();
        PaymentRequest paymentRequest = PaymentRequest.builder()
                .amount(BigDecimal.valueOf(200.0))
                .receiverAccountNumber("123456789")
                .senderAccountNumber("987654321")
                .currency(Currency.EUR)
                .paymentDateTime(now)
                .paymentType(PaymentType.OTHER)
                .build();

        Payment payment = paymentMapper.paymentRequestToPayment(paymentRequest);

        assertEquals(now, payment.getPaymentDateTime());
        assertEquals(PaymentType.OTHER, payment.getPaymentType());
        assertEquals(PaymentStatus.NEW, payment.getPaymentStatus());
        assertEquals(BigDecimal.valueOf(200.0), payment.getAmount());
        assertEquals("123456789", payment.getReceiverAccountNumber());
        assertEquals("987654321", payment.getSenderAccountNumber());
        assertEquals(Currency.EUR, payment.getCurrency());
    }

    @Test
    void testPaymentToPaymentResponse() {
        LocalDateTime now = LocalDateTime.now();
        Payment payment = Payment.builder()
                .amount(BigDecimal.valueOf(300.0))
                .receiverAccountNumber("123456789")
                .senderAccountNumber("987654321")
                .currency(Currency.GBP)
                .paymentDateTime(now)
                .paymentType(PaymentType.OTHER)
                .paymentStatus(PaymentStatus.NEW)
                .createdAt(now)
                .build();

        PaymentResponse paymentResponse = paymentMapper.paymentToPaymentResponse(payment);

        assertEquals(BigDecimal.valueOf(300.0), paymentResponse.amount());
        assertEquals("123456789", paymentResponse.receiverAccountNumber());
        assertEquals("987654321", paymentResponse.senderAccountNumber());
        assertEquals(Currency.GBP, paymentResponse.currency());
        assertEquals(now, paymentResponse.paymentDateTime());
        assertEquals(PaymentType.OTHER, paymentResponse.paymentType());
        assertEquals(PaymentStatus.NEW, paymentResponse.paymentStatus());
    }
}