package faang.school.paymentservice.service;

import faang.school.paymentservice.dto.PaymentDto;
import faang.school.paymentservice.dto.PaymentResponse;
import faang.school.paymentservice.dto.PaymentStatus;
import faang.school.paymentservice.entity.Payment;
import faang.school.paymentservice.exception.PaymentException;
import faang.school.paymentservice.kafka.producer.PaymentResponseProducer;
import faang.school.paymentservice.kafka.producer.UserServicePaymentResponseProducer;
import faang.school.paymentservice.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Objects;
import java.util.Random;

/**
 * @author Evgenii Malkov
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final PaymentResponseProducer paymentResponseProducer;
    private final UserServicePaymentResponseProducer userServicePaymentResponseProducer;
    private final PaymentRepository paymentRepository;

    public PaymentResponse doPayment(PaymentDto paymentDto) {
        Payment payment = findPaymentByRequestId(paymentDto.requestId());
        LocalDateTime expire = payment.getExpiredDateTime();
        long paidTime = Instant.now().toEpochMilli();

        if (!payment.getStatus().equals(PaymentStatus.CREATED.toString())) {
            throw new PaymentException("Payment has already been made, requestId: " + payment.getRequestId());
        }

        if (payment.getExpiredDateTime().toInstant(ZoneOffset.UTC).toEpochMilli() < paidTime) {
            throw new PaymentException(String.format("Payment request expired at: %s, request: %s",
                    expire, paymentDto.requestId()));
        }

        if ((payment.getAmount().compareTo(paymentDto.amount()) != 0)
                || (!payment.getCurrency().equals(paymentDto.currency().toString()))
                || (!payment.getProduct().equals(paymentDto.product().toString()))) {
            throw new PaymentException(String.format("Payment details are invalid, requestId: %s",
                    paymentDto.requestId()));
        }
        PaymentStatus status = pay();
        log.info("Payment status {}, requestId: {}", status, paymentDto.requestId());
        return sendPaymentResponse(paymentDto, status, paidTime);
    }

    private PaymentResponse sendPaymentResponse(PaymentDto paymentDto, PaymentStatus status, long paidTime) {
        long paymentNumber = generatePaymentNumber(paymentDto);
        PaymentResponse response = new PaymentResponse(
                status,
                paymentDto.requestId(),
                paymentDto.product(),
                paymentNumber,
                paidTime);
        sendResponses(response);
        return response;
    }

    private void sendResponses(PaymentResponse paymentResponse) {
        paymentResponseProducer.sendPaymentInfo(paymentResponse);
        userServicePaymentResponseProducer.sendPaymentInfo(paymentResponse);
    }

    private long generatePaymentNumber(PaymentDto paymentDto) {
        long hash = Objects.hash(paymentDto.requestId(), paymentDto.amount(), paymentDto.currency());
        return Math.abs(hash);
    }

    @Transactional
    public void savePaymentData(PaymentResponse paymentResponse) {
        LocalDateTime paidDateTime = LocalDateTime.ofInstant(
                Instant.ofEpochMilli(paymentResponse.paidDateTime()), ZoneId.systemDefault());
        Payment payment = findPaymentByRequestId(paymentResponse.requestId());
        payment.setPaymentNumber(paymentResponse.paymentNumber());
        payment.setStatus(paymentResponse.status().toString());
        payment.setPaidDateTime(paidDateTime);
        paymentRepository.save(payment);
    }

    private Payment findPaymentByRequestId(String requestId) {
        Payment payment = paymentRepository.findByRequestId(requestId);
        if (payment == null) {
            throw new PaymentException("Payment not found, requestId: " + requestId);
        }
        return payment;
    }

    private PaymentStatus pay() {
        // TODO: Подключить логику обращения к платежной системе
        log.info("Something payment magic");
        Random random = new Random();
        return random.nextInt() % 7 != 0 ? PaymentStatus.SUCCESS : PaymentStatus.REJECT;
    }
}
