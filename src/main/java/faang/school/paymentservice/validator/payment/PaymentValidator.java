package faang.school.paymentservice.validator.payment;

import faang.school.paymentservice.dto.payment.PaymentDtoToCreate;
import faang.school.paymentservice.enums.PaymentStatus;
import faang.school.paymentservice.exception.IdempotencyException;
import faang.school.paymentservice.exception.NotEnoughMoneyOnBalanceException;
import faang.school.paymentservice.exception.PaymentException;
import faang.school.paymentservice.model.Balance;
import faang.school.paymentservice.model.Payment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentValidator {

    public void validatePaymentOnSameIdempotencyToken(PaymentDtoToCreate dto, Payment payment) {
        boolean isIdempotency = checkPaymentWithSameUUID(dto, payment);
        if (!isIdempotency) {
            throw new IdempotencyException("This payment has already been made with other details! Try again!");
        }
    }

    public void validateSenderHaveEnoughMoneyOnBalance(Balance senderBalance, PaymentDtoToCreate dto) {
        if (senderBalance.getAuthorizationBalance().compareTo(dto.getAmount()) < 0) {
            throw new NotEnoughMoneyOnBalanceException("Not enough money");
        }
    }

    public void validatePaymentStatusForCancel(Payment payment) {
        if (payment.getPaymentStatus() != PaymentStatus.NEW &&
                payment.getPaymentStatus() != PaymentStatus.READY_TO_CLEAR) {
            throw new PaymentException("It is impossible to perform this operation with this payment, since it has already been closed");
        }
    }

    private boolean checkPaymentWithSameUUID(PaymentDtoToCreate newPayment, Payment oldPayment) {
        return newPayment.getSenderAccountNumber().equals(oldPayment.getSenderAccountNumber())
                && newPayment.getReceiverAccountNumber().equals(oldPayment.getReceiverAccountNumber())
                && newPayment.getAmount().compareTo(oldPayment.getAmount()) == 0
                && newPayment.getCurrency() == oldPayment.getCurrency();
    }
}
