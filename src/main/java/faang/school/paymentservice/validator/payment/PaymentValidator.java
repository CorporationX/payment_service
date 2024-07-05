package faang.school.paymentservice.validator.payment;

import faang.school.paymentservice.dto.payment.PaymentDtoToCreate;
import faang.school.paymentservice.enums.PaymentStatus;
import faang.school.paymentservice.exception.DataValidationException;
import faang.school.paymentservice.model.Balance;
import faang.school.paymentservice.model.Payment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class PaymentValidator {

    public void validatePaymentOnSameIdempotencyToken(PaymentDtoToCreate dto, Payment payment) {
        if (!checkPaymentWithSameUUID(dto, payment)) {
            throw new DataValidationException("This payment has already been made with other details! Try again!");
        }
    }

    public void validateSenderHaveEnoughMoneyOnAuthorizationBalance(Balance senderBalance, PaymentDtoToCreate dto) {
        if (senderBalance.getAuthorizationBalance().compareTo(dto.getAmount()) < 0) {
            throw new DataValidationException("Not enough money");
        }
    }

    public void validatePaymentStatusIsAlreadyCorrect(Payment payment, PaymentStatus status) {
        if(payment.getPaymentStatus().equals(status)) {
            throw new DataValidationException(
                    String.format("Payment status is already %s", payment.getPaymentStatus()));
        }
    }

    private boolean checkPaymentWithSameUUID(PaymentDtoToCreate newPayment, Payment oldPayment) {
        return newPayment.getSenderAccountNumber().equals(oldPayment.getSenderAccountNumber())
                && newPayment.getReceiverAccountNumber().equals(oldPayment.getReceiverAccountNumber())
                && newPayment.getAmount().compareTo(oldPayment.getAmount()) == 0
                && newPayment.getCurrency() == oldPayment.getCurrency();
    }

    public void validateAmountIsPositive(PaymentDtoToCreate dto) {
        if (dto.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new DataValidationException("Amount cannot be negative or zero");
        }
    }
}
