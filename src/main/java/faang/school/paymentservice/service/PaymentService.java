package faang.school.paymentservice.service;

import faang.school.paymentservice.client.AccountClient;
import faang.school.paymentservice.config.kafka.KafkaProducer;
import faang.school.paymentservice.dto.Currency;
import faang.school.paymentservice.dto.PaymentStatus;
import faang.school.paymentservice.dto.account.AccountDto;
import faang.school.paymentservice.dto.payment.AuthorizationEvent;
import faang.school.paymentservice.dto.payment.AuthorizationMessage;
import faang.school.paymentservice.dto.payment.AuthorizationResponse;
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
        AccountDto recipientAccountDto = accountClient.getAccount(message.getRecipientNumber());
        validateCurrency(recipientAccountDto, message.getCurrency());

        // верификационный код
        String verificationCode = String.valueOf(new Random().nextInt(1000, 10000));

        // сохранить в реквест в бд
        Request request = Request.builder()
                .senderNumber(message.getSenderNumber())
                .recipientNumber(message.getRecipientNumber())
                .currency(message.getCurrency())
                .amount(message.getAmount())
                .verificationCode(verificationCode)
                .clearScheduledAt(LocalDateTime.now().plusMinutes(5))
                .status(PaymentStatus.PENDING)
                .build();

        Request createdRequest = paymentRepository.save(request);

        AuthorizationEvent authorizationEvent = AuthorizationEvent.builder()
                .recipientId(recipientAccountDto.getId())
                .amount(message.getAmount())
                .build();

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
