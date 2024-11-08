package faang.school.paymentservice.validator;

import faang.school.paymentservice.config.context.UserContext;
import faang.school.paymentservice.exception.OperationNotPermittedException;
import faang.school.paymentservice.model.dto.AccountDto;
import faang.school.paymentservice.model.dto.PaymentDto;
import faang.school.paymentservice.model.entity.Payment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ValidatorPaymentService {
    public void ownerNumberCorrect(PaymentDto paymentDto, AccountDto accountDto, UserContext userContext) {
        if (!paymentDto.getOwnerAccountNumber().equals(accountDto.getNumber()) ||
                userContext.getUserId() != accountDto.getUserId()) {
            throw new OperationNotPermittedException("Operation prohibited, this number"
                    + paymentDto.getOwnerAccountNumber() + " " + " does not belong to the userId " + userContext.getUserId());
        }
    }

    public boolean operationExist(Payment payment, PaymentDto paymentDto) {
        return payment.getReceiverAccountNumber().equals(paymentDto.getReceiverAccountNumber()) &&
                payment.getOwnerAccountNumber().equals(paymentDto.getOwnerAccountNumber()) &&
                payment.getAmount().equals(paymentDto.getAmount()) &&
                payment.getCurrency().equals(paymentDto.getCurrency());
    }
}
