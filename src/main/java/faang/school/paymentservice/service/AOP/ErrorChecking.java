package faang.school.paymentservice.service.AOP;

import faang.school.paymentservice.client.AccountClient;
import faang.school.paymentservice.dto.account.AccountDto;
import faang.school.paymentservice.dto.payment.AuthorizationMessage;
import faang.school.paymentservice.exeption.GetAuthorizationBadRequest;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class ErrorChecking {
    private final AccountClient accountClient;

    @Pointcut(value = "execution(* faang.school.paymentservice.service.PaymentService.authorizePayment(faang.school.paymentservice.dto.payment.AuthorizationMessage)) && args(message)", argNames = "message")
    public void errorChecking(AuthorizationMessage message) {}

    @Before(value = "errorChecking(message)", argNames = "message")
    public void checkError(AuthorizationMessage message) {
        validateSenderAndRecipient(message);
        validateCurrency(message);
    }

    private void validateSenderAndRecipient(AuthorizationMessage message) {
        if (message.getRecipientAccountId().equals(message.getSenderAccountId())) {
            throw new GetAuthorizationBadRequest("Sender and recipient cannot be the same");
        }
        if (message.getRecipientAccountNumber().equals(message.getSenderNumber())) {
            throw new GetAuthorizationBadRequest("Sender number and recipient cannot be the same");
        }
    }

    private void validateCurrency(AuthorizationMessage message) {
        AccountDto senderAccountDto = accountClient.getAccount(message.getSenderAccountId());
        if (!senderAccountDto.getCurrency().equals(message.getCurrency())) {
            throw new IllegalArgumentException("Currency does not match");
        }
    }
}
