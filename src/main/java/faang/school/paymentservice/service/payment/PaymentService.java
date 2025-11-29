package faang.school.paymentservice.service.payment;

import faang.school.paymentservice.dto.AuthorizationDto;
import faang.school.paymentservice.dto.TypeOperation;
import faang.school.paymentservice.kafka.dto.KafkaAuthorizationRequestDto;
import faang.school.paymentservice.kafka.dto.KafkaCancelRequestDto;
import faang.school.paymentservice.kafka.dto.KafkaClearingRequestDto;
import faang.school.paymentservice.kafka.producer.KafkaProducerService;
import faang.school.paymentservice.model.BankOperation;
import faang.school.paymentservice.model.PaymentStatus;
import faang.school.paymentservice.repository.BankOperationRepository;
import faang.school.paymentservice.service.transaction.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static faang.school.paymentservice.service.payment.PaymentValidator.validateCancel;
import static faang.school.paymentservice.service.payment.PaymentValidator.validateClearing;

@Service
@RequiredArgsConstructor
public class PaymentService {

    @Value("${spring.kafka.topics.payment.authorization-request}")
    private String authorizationRequestTopic;

    @Value("${spring.kafka.topics.payment.clearing-request}")
    private String clearingRequestTopic;

    @Value("${spring.kafka.topics.payment.cancel-request}")
    private String cancelRequestTopic;

    private final BankOperationRepository bankOperationRepository;
    private final KafkaProducerService kafkaProducerService;

    private final TransactionService transactionService;

    @Transactional
    public UUID processAuthorization(@Valid AuthorizationDto authorizationDto) {
        BankOperation bankOperation = BankOperation.builder()
                .senderAccountId(authorizationDto.senderAccountId())
                .recipientAccountId(authorizationDto.recipientAccountId())
                .amount(authorizationDto.amount())
                .typeOperation(TypeOperation.AUTHORIZATION)
                .productCategory(authorizationDto.productCategory())
                .clearScheduledAt(authorizationDto.clearScheduledAt())
                .status(PaymentStatus.ON_AUTHORIZATION)
                .build();

        BankOperation savedBankOperation = bankOperationRepository.save(bankOperation);

        transactionService.saveTransactionBankOperation(savedBankOperation);

        KafkaAuthorizationRequestDto paymentToSend = new KafkaAuthorizationRequestDto(
                authorizationDto.senderAccountId(),
                authorizationDto.amount(),
                savedBankOperation.getId());

        kafkaProducerService.sendMessage(authorizationRequestTopic, paymentToSend);
        return savedBankOperation.getId();
    }

    @Transactional
    public void retryAuthorization(UUID operationId) {
        BankOperation bankOperation = bankOperationRepository.findByIdOrThrow(operationId);

        bankOperation.setTypeOperation(TypeOperation.AUTHORIZATION);
        bankOperation.setStatus(PaymentStatus.ON_AUTHORIZATION);
        bankOperationRepository.save(bankOperation);

        transactionService.saveTransactionBankOperation(bankOperation);

        KafkaAuthorizationRequestDto paymentToSend = new KafkaAuthorizationRequestDto(
                bankOperation.getSenderAccountId(),
                bankOperation.getAmount(),
                bankOperation.getId());

        kafkaProducerService.sendMessage(authorizationRequestTopic, paymentToSend);
    }

    @Transactional
    public void clearingOperation(UUID operationId) {
        BankOperation bankOperation = bankOperationRepository.findByIdOrThrow(operationId);

        validateClearing(bankOperation);

        bankOperation.setTypeOperation(TypeOperation.CLEARING);
        bankOperation.setStatus(PaymentStatus.ON_CLEARING);
        bankOperationRepository.save(bankOperation);

        transactionService.saveTransactionBankOperation(bankOperation);

        KafkaClearingRequestDto paymentToSend = new KafkaClearingRequestDto(
                bankOperation.getSenderAccountId(),
                bankOperation.getRecipientAccountId(),
                bankOperation.getAmount(),
                bankOperation.getId());
        kafkaProducerService.sendMessage(clearingRequestTopic, paymentToSend);
    }

    @Transactional
    public void cancelOperation(UUID operationId) {
        BankOperation bankOperation = bankOperationRepository.findByIdOrThrow(operationId);

        validateCancel(bankOperation);

        bankOperation.setTypeOperation(TypeOperation.CANCELING);
        bankOperation.setStatus(PaymentStatus.ON_CANCELLING);
        bankOperationRepository.save(bankOperation);

        transactionService.saveTransactionBankOperation(bankOperation);

        KafkaCancelRequestDto paymentToSend = new KafkaCancelRequestDto(
                bankOperation.getSenderAccountId(),
                bankOperation.getAmount(),
                bankOperation.getId());
        kafkaProducerService.sendMessage(cancelRequestTopic, paymentToSend);
    }

    public BankOperation getBankOperation(UUID operationId) {
        return bankOperationRepository.findByIdOrThrow(operationId);
    }
}
