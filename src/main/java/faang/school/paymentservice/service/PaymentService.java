package faang.school.paymentservice.service;

import faang.school.paymentservice.client.account_service.AccountServiceClient;
import faang.school.paymentservice.dto.account.AccountDto;
import faang.school.paymentservice.model.Payment;
import faang.school.paymentservice.model.PaymentStatus;
import faang.school.paymentservice.publisher.payment.PaymentEventPublisher;
import faang.school.paymentservice.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

import static faang.school.paymentservice.dto.account.QueryType.NUMBER;
import static faang.school.paymentservice.model.PaymentStatus.AUTH;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final AccountServiceClient accountServiceClient;

    @Transactional
    @PaymentEventPublisher
    public Payment authorizePayment(Payment payment, String accountNumberFrom, String accountNumberTo) {
        validateAmount(payment.getAmount());

        payment.setAccountFromId(getAccountUUID(accountNumberFrom));
        payment.setAccountToId(getAccountUUID(accountNumberTo));
        payment.setStatus(AUTH);

        return paymentRepository.save(payment);
    }

    @Transactional
    @PaymentEventPublisher
    public Payment changePaymentStatus(UUID paymentId, PaymentStatus status) {
        validateStatusChange(status);

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found for id: " + paymentId));

        validateCurrentStatus(payment, status);

        payment.setStatus(status);
        return paymentRepository.save(payment);
    }

    private void validateAmount(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            log.error("'amount' can't be negative or zero");
            throw new IllegalArgumentException("'amount' can't be negative or zero");
        }
    }

    private void validateStatusChange(PaymentStatus status) {
        if (status.equals(AUTH)) {
            log.error("Can't change payment status to authorization");
            throw new IllegalArgumentException("Can't change payment status to authorization");
        }
    }

    private void validateCurrentStatus(Payment payment, PaymentStatus newStatus) {
        if (!payment.getStatus().equals(AUTH)) {
            log.error("For {} status, payment must be in authorization status", newStatus);
            throw new IllegalStateException("For " + newStatus + " payment must be in authorization status");
        }
    }

    private UUID getAccountUUID(String accountNumber) {
        return accountServiceClient.getAccountByNumber(NUMBER, accountNumber).stream()
                .findFirst()
                .map(AccountDto::getId)
                .orElseThrow(() -> new IllegalArgumentException("The account number doesn't exist"));
    }
}