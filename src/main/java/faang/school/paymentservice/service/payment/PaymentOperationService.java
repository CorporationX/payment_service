package faang.school.paymentservice.service.payment;

import faang.school.paymentservice.model.Currency;
import faang.school.paymentservice.model.PaymentStatus;
import faang.school.paymentservice.dto.payment.PaymentDto;
import faang.school.paymentservice.model.Payment;
import faang.school.paymentservice.dto.payment.PaymentCreateDto;
import faang.school.paymentservice.mapper.PaymentMapper;
import faang.school.paymentservice.publisher.payment.PaymentEventPublisher;
import faang.school.paymentservice.service.exchangerate.CurrencyService;
import faang.school.paymentservice.validator.payment.PaymentValidator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentOperationService {

    private final PaymentMapper paymentMapper;
    private final PaymentValidator paymentValidator;
    private final PaymentEventPublisher eventPublisher;
    private final PaymentStatusService paymentStatusUpdater;
    private final PaymentService paymentService;
    private final CurrencyService currencyService;

    @Value("${currency.baseCurrencyPayment}")
    private Currency baseCurrencyPayment;

    @Transactional
    public PaymentDto sendPayment(PaymentCreateDto dto) {
        log.info("Initiating payment: {}", dto);

        adjustCurrencyToBase(dto);

        paymentValidator.validateIdempotencyKeyIsUnique(dto.getIdempotencyKey());

        Payment payment = paymentService.createAndPersistPayment(dto);
        paymentValidator.validateDifferentAccounts(payment);
        eventPublisher.publishPayment(payment);

        return paymentMapper.toPaymentDto(payment);
    }

    @Transactional
    public PaymentDto cancelPayment(UUID paymentId) {
        log.info("Canceling payment with id = {}", paymentId);

        Payment payment = paymentService.getPaymentById(paymentId);
        paymentValidator.validateCancelableStatus(payment);

        return updatePaymentStatusAndPublish(payment, PaymentStatus.CANCEL_PENDING);
    }

    @Transactional
    public PaymentDto confirmPayment(UUID paymentId) {
        log.info("Confirming payment with id = {}", paymentId);

        Payment payment = paymentService.getPaymentById(paymentId);
        paymentValidator.validateConfirmableStatus(payment);

        return updatePaymentStatusAndPublish(payment, PaymentStatus.CONFIRM_PENDING);
    }

    @Transactional
    public void processClearingPayments() {
        List<Payment> payments = paymentService.getReadyForClearingPayments();

        payments.forEach(payment -> updatePaymentStatusAndPublish(payment, PaymentStatus.CLEAR_PENDING));
    }

    private PaymentDto updatePaymentStatusAndPublish(Payment payment, PaymentStatus status) {
        Payment updatedPayment = paymentStatusUpdater.updatePaymentStatus(payment, status);
        eventPublisher.publishPayment(updatedPayment);

        return paymentMapper.toPaymentDto(updatedPayment);
    }

    private void adjustCurrencyToBase(PaymentCreateDto dto) {
        Currency currency = Currency.valueOf(dto.getCurrency());
        if (currency != baseCurrencyPayment) {
            BigDecimal amount = currencyService.convertCurrency(dto.getAmount(), currency, baseCurrencyPayment);
            dto.setAmount(amount);
            dto.setCurrency(baseCurrencyPayment.name());
        }
    }
}

