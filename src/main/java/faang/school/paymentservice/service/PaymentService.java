package faang.school.paymentservice.service;

import faang.school.paymentservice.entity.Payment;
import faang.school.paymentservice.enums.PaymentStatus;
import faang.school.paymentservice.exception.DuplicatePaymentException;
import faang.school.paymentservice.exception.PaymentNotFoundException;
import faang.school.paymentservice.exception.PaymentOperationException;
import faang.school.paymentservice.repository.PaymentRepository;
import jakarta.persistence.OptimisticLockException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final PaymentCheckService paymentCheckService;

    @Transactional
    public Payment createPayment(Payment payment) throws DuplicatePaymentException {
        log.info("Creating payment {}", payment.getId());

        paymentCheckService.checkNewPayment(payment);
        payment = paymentRepository.save(payment);
        paymentCheckService.addNewPayment(payment);

        return payment;
    }

    @Transactional
    public Payment cancelPayment(UUID paymentId) throws PaymentOperationException {
        log.info("Canceling payment {}", paymentId);

        Payment payment = paymentRepository.findById(paymentId).orElseThrow(
                () -> new PaymentOperationException("Canceling failed. Payment %s not found".formatted(paymentId))
        );
        PaymentStatus currentStatus = payment.getPaymentStatus();

        if (currentStatus != PaymentStatus.AUTHORIZED && currentStatus != PaymentStatus.NEW) {
            throw new PaymentOperationException("Cancel failed. Payment %s is not authorized".formatted(paymentId));
        }
        if (currentStatus == PaymentStatus.NEW) {
            payment.setPaymentStatus(PaymentStatus.CANCELLED);
        } else {
            payment.setPaymentStatus(PaymentStatus.PROCESS_OF_CANCELLATION);
        }

        try {
            return paymentRepository.save(payment);
        } catch (OptimisticLockException e) {
            log.warn("OptimisticLockException occurred while cancelling payment {}", paymentId);
            payment.setPaymentStatus(currentStatus);
            return payment;
        }
    }

    @Transactional
    public Payment clearingPayment(UUID paymentId) {
        log.info("Clearing payment {}", paymentId);

        Payment payment = paymentRepository.findById(paymentId).orElseThrow(
                () -> new PaymentOperationException("Clearing failed. Payment %s not found".formatted(paymentId))
        );
        PaymentStatus currentStatus = payment.getPaymentStatus();

        if (payment.getPaymentStatus() != PaymentStatus.AUTHORIZED) {
            throw new PaymentOperationException("Clearing failed. Payment %s is not authorized".formatted(paymentId));
        }

        payment.setPaymentStatus(PaymentStatus.PROCESS_OF_CLEARING);
        payment.setPaymentDateTime(LocalDateTime.now());

        try {
            return paymentRepository.save(payment);
        } catch (OptimisticLockException e) {
            log.warn("OptimisticLockException occurred while clearing payment {}", paymentId);
            payment.setPaymentStatus(currentStatus);
            return payment;
        }
    }

    @Transactional(readOnly = true)
    public Payment getPaymentById(UUID paymentId) throws PaymentNotFoundException {
        return paymentRepository.findById(paymentId).orElseThrow(
                () -> new PaymentNotFoundException("Payment %s not found".formatted(paymentId))
        );
    }

    @Transactional
    public void savePayment(Payment payment) {
        paymentRepository.save(payment);
    }

    @Transactional(readOnly = true)
    public List<Payment> getNewPayments() {
        return paymentRepository.findAllByPaymentStatus(PaymentStatus.NEW);
    }

    @Transactional
    public List<Payment> getCancelPayments() {
        return paymentRepository.findAllByPaymentStatus(PaymentStatus.PROCESS_OF_CANCELLATION);
    }

    @Transactional(readOnly = true)
    public List<Payment> getPaymentsByClearing() {
        List<Payment> manualClearingPayments = paymentRepository
                .findAllByPaymentStatus(PaymentStatus.PROCESS_OF_CLEARING);

        List<Payment> autoClearingPayments = paymentRepository
                .findAllByPaymentStatusAndPaymentDateTimeBefore(PaymentStatus.AUTHORIZED,
                        LocalDateTime.now());

        List<Payment> resultList = new ArrayList<>();
        resultList.addAll(manualClearingPayments);
        resultList.addAll(autoClearingPayments);

        return resultList;
    }

    @Transactional
    public void setPaymentErrorStatus(UUID paymentId) {
        paymentRepository.setErrorPaymentStatus(paymentId);
    }
}
