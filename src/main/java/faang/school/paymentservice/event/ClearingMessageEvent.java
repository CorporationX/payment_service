package faang.school.paymentservice.event;

import faang.school.paymentservice.dto.PaymentStatus;

import java.math.BigDecimal;

public class ClearingMessageEvent {
    private String accountNumber;

    private BigDecimal amount;

    private PaymentStatus paymentStatus;
}
