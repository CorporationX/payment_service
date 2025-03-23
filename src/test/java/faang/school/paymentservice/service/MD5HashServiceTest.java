package faang.school.paymentservice.service;

import faang.school.paymentservice.entity.Payment;
import faang.school.paymentservice.enums.Currency;
import faang.school.paymentservice.enums.PaymentType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

class MD5HashServiceTest {
    private final MD5HashService md5HashService = new MD5HashService();

    @Test
    void getPaymentHash() {
        String expected = "2B7EAB45E6CD0C2E214F83E971A298D0";
        Payment payment = Payment.builder()
                .senderAccountNumber("000000000001")
                .receiverAccountNumber("000000000002")
                .amount(BigDecimal.valueOf(100))
                .currency(Currency.RUB)
                .paymentType(PaymentType.OTHER)
                .build();
        String actual = md5HashService.getPaymentHash(payment);

        Assertions.assertEquals(expected, actual);
    }
}