package faang.school.paymentservice.service.payment;

import faang.school.paymentservice.dto.AuthorizationDto;
import faang.school.paymentservice.dto.BankOperationDto;
import faang.school.paymentservice.dto.TypeOperation;
import faang.school.paymentservice.exception.DuplicateRequestException;
import faang.school.paymentservice.kafka.dto.AuthorizationKafkaRequestDto;
import faang.school.paymentservice.kafka.dto.CancelKafkaRequestDto;
import faang.school.paymentservice.kafka.dto.ClearingKafkaRequestDto;
import faang.school.paymentservice.kafka.producer.KafkaProducerService;
import faang.school.paymentservice.model.PaymentStatus;
import faang.school.paymentservice.model.Transfer;
import faang.school.paymentservice.repository.TransferRepository;
import faang.school.paymentservice.service.redis.RedisService;
import faang.school.paymentservice.service.transaction.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static faang.school.paymentservice.service.payment.PaymentValidator.validateCancel;
import static faang.school.paymentservice.service.payment.PaymentValidator.validateClearing;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final TransferRepository transferRepository;
    private final KafkaProducerService kafkaProducerService;
    private final TransactionService transactionService;
    private final RedisService redisService;

    @Value("${spring.kafka.topics.payment.authorization-request}")
    private String authorizationRequestTopic;

    @Value("${spring.kafka.topics.payment.clearing-request}")
    private String clearingRequestTopic;

    @Value("${spring.kafka.topics.payment.cancel-request}")
    private String cancelRequestTopic;

    @Value("${app.clear-scheduled-at}")
    private Long clearScheduledAt;

    @Value("${app.request-ttl}")
    private Long requestTTL;

    @Transactional
    public UUID processAuthorization(@Valid AuthorizationDto authorizationDto) {
        validateIdempotence(authorizationDto);
        Transfer transfer = Transfer.builder()
                .senderAccountId(authorizationDto.senderAccountId())
                .recipientAccountId(authorizationDto.recipientAccountId())
                .amount(authorizationDto.amount())
                .productCategory(authorizationDto.productCategory())
                .clearScheduledAt(LocalDateTime.now().plusSeconds(clearScheduledAt))
                .status(PaymentStatus.ON_AUTHORIZATION)
                .build();

        Transfer savedTransfer = transferRepository.save(transfer);

        transactionService.saveTransfersTransaction(savedTransfer, TypeOperation.AUTHORIZATION);

        AuthorizationKafkaRequestDto paymentToSend = new AuthorizationKafkaRequestDto(
                authorizationDto.senderAccountId(),
                authorizationDto.amount(),
                savedTransfer.getId());

        kafkaProducerService.sendMessage(authorizationRequestTopic, paymentToSend);
        return savedTransfer.getId();
    }

    private void validateIdempotence(AuthorizationDto authorizationDto) {
        String  key = String.valueOf(authorizationDto.hashCode());

        if (redisService.exists(key)) {
            throw new DuplicateRequestException("Duplicate request");
        }

        redisService.set(key, authorizationDto, requestTTL, TimeUnit.SECONDS);
    }

    @Transactional
    public void clearingOperation(UUID transferId) {
        Transfer transfer = transferRepository.findByIdOrThrow(transferId);

        if (transfer.getStatus() == PaymentStatus.ON_AUTHORIZATION) {
            transfer.setStatus(PaymentStatus.CLEARING_WAITING);
            Transfer savedTransfer = transferRepository.save(transfer);

            transactionService.saveTransfersTransaction(savedTransfer, TypeOperation.CLEARING);
        } else {
            validateClearing(transfer);

            transfer.setStatus(PaymentStatus.ON_CLEARING);
            Transfer savedTransfer = transferRepository.save(transfer);

            transactionService.saveTransfersTransaction(savedTransfer, TypeOperation.CLEARING);

            ClearingKafkaRequestDto paymentToSend = new ClearingKafkaRequestDto(
                    transfer.getSenderAccountId(),
                    transfer.getRecipientAccountId(),
                    transfer.getAmount(),
                    transfer.getId());
            kafkaProducerService.sendMessage(clearingRequestTopic, paymentToSend);
        }
    }

    @Transactional
    public void cancelOperation(UUID transferId) {
        Transfer transfer = transferRepository.findByIdOrThrow(transferId);

        if (transfer.getStatus() == PaymentStatus.ON_AUTHORIZATION) {
            transfer.setStatus(PaymentStatus.CANCEL_WAITING);
            Transfer savedTransfer = transferRepository.save(transfer);

            transactionService.saveTransfersTransaction(savedTransfer, TypeOperation.CANCELING);
        } else {
            validateCancel(transfer);

            transfer.setStatus(PaymentStatus.ON_CANCELLING);
            Transfer savedTransfer = transferRepository.save(transfer);

            transactionService.saveTransfersTransaction(savedTransfer, TypeOperation.CANCELING);

            CancelKafkaRequestDto paymentToSend = new CancelKafkaRequestDto(
                    transfer.getSenderAccountId(),
                    transfer.getAmount(),
                    transfer.getId());
            kafkaProducerService.sendMessage(cancelRequestTopic, paymentToSend);
        }
    }

    public BankOperationDto getBankOperation(UUID operationId) {
        Transfer transfer = transferRepository.findByIdOrThrow(operationId);
        return new BankOperationDto(
                transfer.getSenderAccountId(),
                transfer.getRecipientAccountId(),
                transfer.getAmount(),
                transfer.getProductCategory(),
                transfer.getClearScheduledAt(),
                transfer.getStatus()
        );
    }
}
