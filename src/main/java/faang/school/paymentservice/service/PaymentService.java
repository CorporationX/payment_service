package faang.school.paymentservice.service;

import faang.school.paymentservice.client.account_service.AccountServiceClient;
import faang.school.paymentservice.dto.account.AccountDto;
import faang.school.paymentservice.model.Currency;
import faang.school.paymentservice.model.Payment;
import faang.school.paymentservice.model.PaymentStatus;
import faang.school.paymentservice.publisher.payment.PublishPaymentEvent;
import faang.school.paymentservice.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
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

    @Transactional
    @PublishPaymentEvent
    public Payment authorizePayment(Payment payment, String accountNumberFrom, String accountNumberTo) {
        validateAmount(payment.getAmount());
        AccountDto from = getAccount(accountNumberFrom);
        AccountDto to = getAccount(accountNumberTo);
        validateAccountStatus(from);
        validateAccountStatus(to);
        validatePaymentCurrency(payment, from, to);

        payment.setAccountFromId(from.getId());
        payment.setAccountToId(to.getId());
        payment.setStatus(AUTH_PENDING);

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
        validateCurrentPaymentResponceStatus(status, payment);
        payment.setStatus(status);
    }

    @Transactional(readOnly = true)
    public List<Payment> getPaymentsForClearing() {
        return paymentRepository.getPaymentsForClearing();
    }

    private void validateResponceStatusForUpdate(PaymentStatus status) {
        List<PaymentStatus> correctUpdateStatus =
                List.of(AUTH_ERROR, AUTH_SUCCESS, SCHEDULED_SUCCESS, CANCEL_SUCCESS, FORCED_SUCCESS);
        if (!correctUpdateStatus.contains(status)) {
            log.error("Incorrect status for update");
            throw new IllegalArgumentException("Incorrect status for update");
        }
    }

    private void validateCurrentPaymentResponceStatus(PaymentStatus status, Payment payment) {
        boolean correctStatus = switch (status) {
            case AUTH_ERROR, AUTH_SUCCESS -> payment.getStatus().equals(AUTH_PENDING);
            case SCHEDULED_SUCCESS -> payment.getStatus().equals(SCHEDULED_PENDING);
            case CANCEL_SUCCESS -> payment.getStatus().equals(CANCEL_PENDING);
            case FORCED_SUCCESS -> payment.getStatus().equals(FORCED_PENDING);
            default -> false;
        };

        if (!correctStatus) {
            throw new IllegalStateException("Incorrect update status for current payment status");
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