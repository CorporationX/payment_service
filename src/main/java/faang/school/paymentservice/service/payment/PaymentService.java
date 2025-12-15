package faang.school.paymentservice.service.payment;

import faang.school.paymentservice.dto.AuthorizationDto;
import faang.school.paymentservice.dto.TransferDto;
import faang.school.paymentservice.dto.TypeOperation;
import faang.school.paymentservice.exception.DuplicateRequestException;
import faang.school.paymentservice.exception.ForbiddenException;
import faang.school.paymentservice.kafka.producer.payment.KafkaProducerPaymentService;
import faang.school.paymentservice.model.PaymentStatus;
import faang.school.paymentservice.model.Transfer;
import faang.school.paymentservice.repository.TransferRepository;
import faang.school.paymentservice.service.redis.RedisPaymentService;
import faang.school.paymentservice.service.transaction.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

import static faang.school.paymentservice.mapper.TransferMapper.toTransfer;
import static faang.school.paymentservice.mapper.TransferMapper.toTransferDto;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final TransferRepository transferRepository;
    private final TransactionService transactionService;
    private final KafkaProducerPaymentService kafkaProducerPaymentService;

    private final RedisPaymentService redisPaymentService;

    @Value("${app.clear-scheduled-at}")
    private Long clearScheduledAt;

    @Transactional
    public UUID processAuthorization(@Valid AuthorizationDto authorizationDto) {
        redisPaymentService.validateAuthorizationIdempotence(authorizationDto);
        Transfer transfer = toTransfer(authorizationDto);
        transfer.setClearScheduledAt(LocalDateTime.now().plusSeconds(clearScheduledAt));

        Transfer savedTransfer = transferRepository.save(transfer);

        transactionService.saveTransfersTransaction(savedTransfer, TypeOperation.AUTHORIZATION);

        kafkaProducerPaymentService.sendAuthorizationRequest(savedTransfer);
        return savedTransfer.getId();
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void clearingOperation(UUID transferId) {
        Transfer transfer = transferRepository.findByIdOrThrow(transferId);

        if (transfer.getStatus() == PaymentStatus.ON_AUTHORIZATION) {
            transfer.setStatus(PaymentStatus.CLEARING_WAITING);
            Transfer savedTransfer = transferRepository.save(transfer);

            transactionService.saveTransfersTransaction(savedTransfer, TypeOperation.CLEARING);
        } else if (!(transfer.getStatus() == PaymentStatus.AUTHORIZATION_SUCCESS)) {
            throw new DuplicateRequestException("Non-idempotent clearing for an operation: %s".formatted(transfer.getId()));
        } else {
            transfer.setStatus(PaymentStatus.ON_CLEARING);
            Transfer savedTransfer = transferRepository.save(transfer);

            transactionService.saveTransfersTransaction(savedTransfer, TypeOperation.CLEARING);

            kafkaProducerPaymentService.sendClearingRequest(savedTransfer);
        }
    }

    @Transactional
    public void cancelOperation(UUID transferId) {
        Transfer transfer = transferRepository.findByIdOrThrow(transferId);

        if (transfer.getStatus() == PaymentStatus.ON_AUTHORIZATION) {
            transfer.setStatus(PaymentStatus.CANCEL_WAITING);
            Transfer savedTransfer = transferRepository.save(transfer);

            transactionService.saveTransfersTransaction(savedTransfer, TypeOperation.CANCELING);
        } else if (!(transfer.getStatus() == PaymentStatus.AUTHORIZATION_SUCCESS)) {
            throw new DuplicateRequestException("Non-idempotent cancel for an operation: %s".formatted(transfer.getId()));
        } else {
            transfer.setStatus(PaymentStatus.ON_CANCELLING);
            Transfer savedTransfer = transferRepository.save(transfer);

            transactionService.saveTransfersTransaction(savedTransfer, TypeOperation.CANCELING);

            kafkaProducerPaymentService.sendCancelRequest(savedTransfer);
        }
    }

    @Transactional(readOnly = true)
    public TransferDto getTransfer(UUID operationId, UUID senderAccountId) {
        Transfer transfer = transferRepository.findByIdOrThrow(operationId);
        if (transfer.getSenderAccountId().equals(senderAccountId)) {
            return toTransferDto(transfer);
        } else throw new ForbiddenException("You cannot view the operations of another user.");
    }
}
