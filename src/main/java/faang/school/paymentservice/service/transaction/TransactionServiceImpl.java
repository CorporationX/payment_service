package faang.school.paymentservice.service.transaction;

import faang.school.paymentservice.dto.transaction.TransactionDtoToCreate;
import faang.school.paymentservice.enums.TransactionStatus;
import faang.school.paymentservice.exception.NotFoundException;
import faang.school.paymentservice.model.Transaction;
import faang.school.paymentservice.repository.TransactionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final TransactionValidator transactionValidator;

    @Transactional
    @Async(value = "paymentPool")
    public void createRequestForPayment(Long transactionId) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new NotFoundException(
                        String.format("Transaction with id %d not found", transactionId)));

        transactionValidator.validate(transaction);

        transaction.setTransactionStatus(TransactionStatus.READY_TO_CLEAR);

        transaction = transactionRepository.save(transaction);
    }

    @Transactional
    @Async(value = "paymentPool")
    public void clearTransaction(Long transactionId) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new NotFoundException("Transaction not found"));

        // Обновляем статус транзакции на CLEAR
        transaction.setStatus(TransactionStatus.CLEAR);
        transactionRepository.save(transaction);

        // Отправляем сообщение в Redis для клиринга
        RedisPaymentDto redisPaymentDto = new RedisPaymentDto(
                transaction.getUserId(),
                transaction.getId(),
                transaction.getSenderAccountNumber(),
                transaction.getReceiverAccountNumber(),
                transaction.getAmount(),
                transaction.getCurrency(),
                TransactionStatus.CLEAR
        );

        paymentRequestPublisher.publish(redisPaymentDto);
    }

    @Transactional
    public void cancelTransaction(Long transactionId) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new NotFoundException("Transaction not found"));

        // Обновляем статус транзакции на CANCELLED
        transaction.setStatus(TransactionStatus.CANCELLED);
        transactionRepository.save(transaction);

        // Отправляем сообщение в Redis для отмены
        RedisPaymentDto redisPaymentDto = new RedisPaymentDto(
                transaction.getUserId(),
                transaction.getId(),
                transaction.getSenderAccountNumber(),
                transaction.getReceiverAccountNumber(),
                transaction.getAmount(),
                transaction.getCurrency(),
                TransactionStatus.CANCELLED
        );

        paymentRequestPublisher.publish(redisPaymentDto);
    }
}