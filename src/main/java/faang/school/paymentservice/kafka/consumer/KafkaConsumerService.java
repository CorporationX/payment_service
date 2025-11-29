package faang.school.paymentservice.kafka.consumer;

import faang.school.paymentservice.kafka.dto.KafkaAuthorizationResponseDto;
import faang.school.paymentservice.kafka.dto.KafkaCancelResponseDto;
import faang.school.paymentservice.kafka.dto.KafkaClearingResponseDto;
import faang.school.paymentservice.model.BankOperation;
import faang.school.paymentservice.model.PaymentStatus;
import faang.school.paymentservice.repository.BankOperationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class KafkaConsumerService {

    private final BankOperationRepository bankOperationRepository;

    @Transactional
    public void handleAuthorizationResponse(KafkaAuthorizationResponseDto responseDto) {
        handleResponse(responseDto.operationId(), responseDto.paymentStatus(), responseDto.description());
    }

    @Transactional
    public void handleClearingResponse(KafkaClearingResponseDto responseDto) {
        handleResponse(responseDto.operationId(), responseDto.paymentStatus(), responseDto.description());
    }

    @Transactional
    public void handleCancelResponse(KafkaCancelResponseDto responseDto) {
        handleResponse(responseDto.operationId(), responseDto.paymentStatus(), responseDto.description());
    }

    private void handleResponse(UUID operationId, PaymentStatus paymentStatus, String description) {
        BankOperation bankOperation = bankOperationRepository.findByIdOrThrow(operationId);
        bankOperation.setStatus(paymentStatus);
        bankOperation.setStatusDescription(description);
        bankOperationRepository.save(bankOperation);
    }
}
