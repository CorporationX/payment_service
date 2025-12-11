package faang.school.paymentservice.kafka.consumer;

import faang.school.paymentservice.dto.TypeOperation;
import faang.school.paymentservice.kafka.dto.AuthorizationKafkaResponseDto;
import faang.school.paymentservice.kafka.dto.CancelKafkaResponseDto;
import faang.school.paymentservice.kafka.dto.ClearingKafkaResponseDto;
import faang.school.paymentservice.model.Transfer;
import faang.school.paymentservice.model.PaymentStatus;
import faang.school.paymentservice.repository.TransferRepository;
import faang.school.paymentservice.service.payment.PaymentService;
import faang.school.paymentservice.service.transaction.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class KafkaConsumerService {

    private final TransferRepository transferRepository;
    private final TransactionService transactionService;
    private final PaymentService paymentService;

    @Transactional
    public void handleAuthorizationResponse(AuthorizationKafkaResponseDto responseDto) {
        Transfer transfer = transferRepository.findByIdOrThrow(responseDto.transferId());
        PaymentStatus transferStatus = transfer.getStatus();
        transfer.setStatus(responseDto.paymentStatus());
        transfer.setStatusDescription(responseDto.description());
        Transfer savedTransfer = transferRepository.save(transfer);
        transactionService.saveTransfersTransaction(savedTransfer, TypeOperation.AUTHORIZATION);
        checkForWaitingOperations(responseDto.paymentStatus(), transferStatus, responseDto.transferId());
    }

    @Transactional
    public void handleClearingResponse(ClearingKafkaResponseDto responseDto) {
        handleResponse(responseDto.transferId(), responseDto.paymentStatus(), responseDto.description(), TypeOperation.CLEARING);
    }

    @Transactional
    public void handleCancelResponse(CancelKafkaResponseDto responseDto) {
        handleResponse(responseDto.transferId(), responseDto.paymentStatus(), responseDto.description(), TypeOperation.CANCELING);
    }

    private void handleResponse(UUID operationId, PaymentStatus paymentStatus, String description, TypeOperation typeOperation) {
        Transfer transfer = transferRepository.findByIdOrThrow(operationId);
        transfer.setStatus(paymentStatus);
        transfer.setStatusDescription(description);
        Transfer savedTransfer = transferRepository.save(transfer);
        transactionService.saveTransfersTransaction(savedTransfer, typeOperation);
    }

    private void checkForWaitingOperations(PaymentStatus responseStatus, PaymentStatus existTransferStatus, UUID transferId) {
        if (responseStatus == PaymentStatus.AUTHORIZATION_SUCCESS) {
            if (existTransferStatus == PaymentStatus.CLEARING_WAITING) {
                paymentService.clearingOperation(transferId);
            }
            if (existTransferStatus == PaymentStatus.CANCEL_WAITING) {
                paymentService.cancelOperation(transferId);
            }
        }
    }
}
