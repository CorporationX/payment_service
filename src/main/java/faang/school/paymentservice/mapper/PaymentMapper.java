package faang.school.paymentservice.mapper;

import faang.school.paymentservice.dto.PaymentRequest;
import faang.school.paymentservice.dto.PaymentResponse;
import faang.school.paymentservice.entity.Payment;
import faang.school.paymentservice.enums.PaymentStatus;
import faang.school.paymentservice.enums.PaymentType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class PaymentMapper {

    @Value("${payment.valid-interval-minutes}")
    private final int paymentsValidIntervalInMinutes = 1;

    public Payment paymentRequestToPayment(PaymentRequest paymentRequest) {
        if (paymentRequest.getPaymentDateTime() == null) {
            LocalDateTime now = LocalDateTime.now();
            paymentRequest.setPaymentDateTime(
                    now.withMinute(now.getMinute() / paymentsValidIntervalInMinutes * paymentsValidIntervalInMinutes)
                            .withSecond(0)
                            .withNano(0));
        }

        if (paymentRequest.getPaymentType() == null) {
            paymentRequest.setPaymentType(PaymentType.OTHER);
        }

        Payment payment = Payment.builder()
                .paymentStatus(PaymentStatus.NEW)
                .createdAt(LocalDateTime.now())
                .amount(paymentRequest.getAmount())
                .receiverAccountNumber(paymentRequest.getReceiverAccountNumber())
                .senderAccountNumber(paymentRequest.getSenderAccountNumber())
                .currency(paymentRequest.getCurrency())
                .paymentDateTime(paymentRequest.getPaymentDateTime())
                .paymentType(paymentRequest.getPaymentType())
                .build();


        return payment;
    }

    public PaymentResponse paymentToPaymentResponse(Payment payment) {
        return PaymentResponse.builder()
                .id(payment.getId())
                .senderAccountNumber(payment.getSenderAccountNumber())
                .receiverAccountNumber(payment.getReceiverAccountNumber())
                .amount(payment.getAmount())
                .currency(payment.getCurrency())
                .createdAt(payment.getPaymentDateTime())
                .paymentDateTime(payment.getPaymentDateTime())
                .paymentStatus(payment.getPaymentStatus())
                .paymentType(payment.getPaymentType())
                .build();
    }
}
