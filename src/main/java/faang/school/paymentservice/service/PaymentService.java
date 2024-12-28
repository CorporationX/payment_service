package faang.school.paymentservice.service;

import faang.school.paymentservice.client.AccountClient;
import faang.school.paymentservice.dto.Currency;
import faang.school.paymentservice.dto.PaymentStatus;
import faang.school.paymentservice.dto.account.AccountDto;
import faang.school.paymentservice.dto.payment.AuthorizationEvent;
import faang.school.paymentservice.dto.payment.AuthorizationMessage;
import faang.school.paymentservice.dto.payment.AuthorizationResponse;
import faang.school.paymentservice.dto.payment.ClearingPaymentResponse;
import faang.school.paymentservice.model.Request;
import faang.school.paymentservice.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final AccountClient accountClient;

    public AuthorizationResponse authorizePayment(AuthorizationMessage message) {
        // валидация сообщения
        AccountDto senderAccountDto = accountClient.getAccount(message.getSenderNumber());
        validateCurrency(senderAccountDto, message.getCurrency());

        // верификационный код
        String verificationCode = String.valueOf(new Random().nextLong(1000, 1000000000000L));

        // сохранить в реквест в бд
        Request request = Request.builder()
                .senderNumber(message.getSenderNumber())
                .recipientNumber(message.getRecipientAccountNumber())
                .currency(message.getCurrency())
                .amount(message.getAmount())
                .verificationCode(verificationCode)
                .clearScheduledAt(LocalDateTime.now().plusMinutes(5))
                .status(PaymentStatus.PENDING)
                .build();

        //сохроняем в бд request
        Request createdRequest = paymentRepository.save(request);

        AuthorizationEvent authorizationEvent = AuthorizationEvent.builder()
                .verificationCode(verificationCode)
                .recipientAccountId(message.getRecipientAccountId())
                .senderAccountId(message.getSenderAccountId())
                .recipientAccountId(message.getRecipientAccountId())
                .amount(message.getAmount())
                .build();

        log.warn("authorizationEvent  -------------------------: {}", authorizationEvent);

        // публикуем сообщение
        kafkaTemplate.send("authorization-topic", authorizationEvent);

        return AuthorizationResponse.builder()
                .requestId(createdRequest.getId())
                .verificationCode(verificationCode)
                .build();
    }

    private void validateCurrency(AccountDto accountDto, Currency currency) {
        if (!accountDto.getCurrency().equals(currency)) {
            throw new IllegalArgumentException("Currency does not match");
        }
    }
}
