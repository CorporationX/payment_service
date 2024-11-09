package faang.school.paymentservice.service.payment;

import faang.school.paymentservice.aspect.PublishPaymentEvent;
import faang.school.paymentservice.client.account_service.AccountServiceClient;
import faang.school.paymentservice.dto.account.AccountDto;
import faang.school.paymentservice.model.Currency;
import faang.school.paymentservice.model.Payment;
import faang.school.paymentservice.model.PaymentStatus;
import faang.school.paymentservice.repository.PaymentRepository;
import faang.school.paymentservice.service.payment.tools.IdempotenceKeyGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static faang.school.paymentservice.dto.account.QueryType.NUMBER;
import static faang.school.paymentservice.model.PaymentStatus.AUTH_ERROR;
import static faang.school.paymentservice.model.PaymentStatus.AUTH_PENDING;
import static faang.school.paymentservice.model.PaymentStatus.AUTH_SUCCESS;
import static faang.school.paymentservice.model.PaymentStatus.CANCEL_PENDING;
import static faang.school.paymentservice.model.PaymentStatus.CANCEL_SUCCESS;
import static faang.school.paymentservice.model.PaymentStatus.FORCED_PENDING;
import static faang.school.paymentservice.model.PaymentStatus.FORCED_SUCCESS;
import static faang.school.paymentservice.model.PaymentStatus.SCHEDULED_PENDING;
import static faang.school.paymentservice.model.PaymentStatus.SCHEDULED_SUCCESS;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final AccountServiceClient accountServiceClient;
    private final IdempotenceKeyGenerator generator;

    @Transactional
    @PublishPaymentEvent
    public Payment authorizePayment(Payment payment, String accountNumberFrom, String accountNumberTo) {
        validateAmount(payment.getAmount());

        String idempotencyKey = validateIdempotence(payment, accountNumberFrom, accountNumberTo);

        AccountDto from = getAccount(accountNumberFrom);
        AccountDto to = getAccount(accountNumberTo);
        validateAccountStatus(from);
        validateAccountStatus(to);
        validatePaymentCurrency(payment, from, to);

        payment.setAccountFromId(from.getId());
        payment.setAccountToId(to.getId());
        payment.setStatus(AUTH_PENDING);
        payment.setIdempotencyKey(idempotencyKey);

        return paymentRepository.save(payment);
    }

    @Transactional
    @PublishPaymentEvent
    public Payment updatePaymentStatus(UUID paymentId, PaymentStatus status) {
        validateStatusForUpdate(status);
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found for id: " + paymentId));
        validateCurrentStatus(payment, status);
        payment.setStatus(status);
        return paymentRepository.save(payment);
    }

    @Transactional
    public void updatePaymentStatusFromResponce(UUID paymentId, PaymentStatus status) {
        validateResponceStatusForUpdate(status);
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found for id: " + paymentId));
        payment.setStatus(status);
    }

    @Transactional(readOnly = true)
    public List<Payment> getPaymentsForClearing() {
        return paymentRepository.getPaymentsForClearing();
    }

    private String validateIdempotence(Payment payment, String accountNumberFrom, String accountNumberTo) {
        String idempotencyKey = generator.generateIdempotenceKey(
                payment.getAmount().toString(),
                payment.getCurrency().toString(),
                accountNumberFrom,
                accountNumberTo,
                payment.getClearScheduledAt().toString());

        Optional<Payment> paymentOptional = paymentRepository.findByIdempotencyKey(idempotencyKey);
        if (paymentOptional.isPresent()) {
            Payment currentPayment = paymentOptional.get();
            if (isProcessedWithinOneMinute(currentPayment)) {
                throw new IllegalStateException("This payment has already been processed");
            } else {
                String oldIdempotencyKey = currentPayment.getIdempotencyKey();
                String newIdempotencyKey = generator.generateIdempotenceKey(oldIdempotencyKey, LocalDateTime.now().toString());
                currentPayment.setIdempotencyKey(newIdempotencyKey);
                paymentRepository.save(currentPayment);
            }
        }
        return idempotencyKey;
    }

    private boolean isProcessedWithinOneMinute(Payment currentPayment) {
        return LocalDateTime.now().isBefore(currentPayment.getCreatedAt().plusMinutes(1));
    }

    private void validateResponceStatusForUpdate(PaymentStatus status) {
        List<PaymentStatus> correctUpdateStatus =
                List.of(AUTH_ERROR, AUTH_SUCCESS, SCHEDULED_SUCCESS, CANCEL_SUCCESS, FORCED_SUCCESS);
        if (!correctUpdateStatus.contains(status)) {
            log.error("Incorrect status for update");
            throw new IllegalArgumentException("Incorrect status for update");
        }
    }

    private void validateAccountStatus(AccountDto account) {
        if(!account.getAccountStatus().equals(AccountDto.AccountStatus.ACTIVE)) {
            log.error("Account {} is not ACTIVE", account.getAccountNumber());
            throw new IllegalStateException("Account " + account.getAccountNumber() + " is not ACTIVE");
        }
    }

    private void validatePaymentCurrency(Payment payment, AccountDto from, AccountDto to) {
        Currency paymentCurrency = payment.getCurrency();
        Currency fromCurrency = from.getCurrency();
        Currency toCurrency = to.getCurrency();

        if (!paymentCurrency.equals(fromCurrency) || !paymentCurrency.equals(toCurrency)) {
            log.error("Currency type must be the same");
            throw new IllegalArgumentException("Currency type must be the same");
        }
    }

    private void validateAmount(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            log.error("'amount' can't be negative or zero");
            throw new IllegalArgumentException("'amount' can't be negative or zero");
        }
    }

    private void validateStatusForUpdate(PaymentStatus status) {
        if (!status.equals(FORCED_PENDING) && !status.equals(CANCEL_PENDING) && !status.equals(SCHEDULED_PENDING)) {
            log.error("Incorrect status will change");
            throw new IllegalArgumentException("Incorrect status will change");
        }
    }

    private void validateCurrentStatus(Payment payment, PaymentStatus newStatus) {
        if (!payment.getStatus().equals(AUTH_SUCCESS)) {
            log.error("For {} status, payment must be in authorization status", newStatus);
            throw new IllegalStateException("For " + newStatus + " payment must be in AUTH_SUCCESS status");
        }
    }

    private AccountDto getAccount(String accountNumber) {
        return accountServiceClient.getAccountByNumber(NUMBER, accountNumber).stream()
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("The account number doesn't exist"));
    }
}