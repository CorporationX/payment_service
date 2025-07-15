package faang.school.paymentservice.service;

import faang.school.paymentservice.dto.pending.PendingDto;
import faang.school.paymentservice.dto.pending.PendingRequestDto;
import faang.school.paymentservice.dto.pending.PendingResponseDto;
import faang.school.paymentservice.dto.pending.RequestOpenDto;
import faang.school.paymentservice.dto.pending.ResponseClearingDto;
import faang.school.paymentservice.enums.RequestStatus;
import faang.school.paymentservice.kafka.KafkaProducerService;
import faang.school.paymentservice.mapper.PendingMapper;
import faang.school.paymentservice.model.Pending;
import faang.school.paymentservice.repository.PendingRepository;
import faang.school.paymentservice.storage.StatusStoragePendingResponseDto;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PendingService {

    @Value("${kafka.topics.pending-topic}")
    private String requestTopic;

    @Value("${kafka.partitions.partition-request}")
    private int partitionNumberRequest;

    @Value("${kafka.partitions.partition-clearing}")
    private int partitionClearing;

    private final PendingRepository pendingRepository;
    private final KafkaProducerService kafkaProducerService;
    private final PendingMapper pendingMapper;
    private final StatusStoragePendingResponseDto statusStoragePendingResponseDto;

    @Transactional
    public PendingResponseDto authorizationPending(PendingDto pendingDto) {
        Pending pending = pendingMapper.toEntity(pendingDto);

        String operationId = UUID.randomUUID().toString();
        pending.setOperationId(operationId);

        PendingRequestDto requestDto = pendingMapper.toRequest(pendingDto);
        requestDto.setOperationId(operationId);
        requestDto.setToken(pendingDto.token());

        kafkaProducerService.sendPendingResponseDto(requestDto, requestTopic, partitionNumberRequest);
        log.info("Request to account service send with {} and {}", pendingDto.accountNumber(), pendingDto.balance());

        return updatePendingFromPendingResponse(pending, statusStoragePendingResponseDto.saveStatus(operationId));
    }

    public Pending getPendingByOperationId(String operationId) {
        return pendingRepository.findByOperationId(operationId)
                .orElseThrow(() -> new IllegalArgumentException("OperationId " + operationId + " not found"));
    }

    @Transactional
    public void openRequest(RequestOpenDto requestOpenDto) {
        Pending pending = getPendingByOperationId(requestOpenDto.getOperationId());

        pending.setRequestStatus(requestOpenDto.getStatus());
        pending.setReason(requestOpenDto.getReason());
    }

    @Transactional
    public PendingResponseDto forcedClearing(String operationId) {
        Pending pending = getPendingByOperationId(operationId);

        if (!pending.getRequestStatus().equals(RequestStatus.PENDING)) {
            throw new IllegalArgumentException("Operation " + operationId + " is not ready for clearing yet");
        }

        ResponseClearingDto clearingDto = pendingMapper.toClearing(pending);
        clearingDto.setForced(true);

        kafkaProducerService.sendResponseClearing(clearingDto, requestTopic, partitionClearing);

        return updatePendingFromPendingResponse(pending, statusStoragePendingResponseDto.saveStatus(operationId));
    }

    @Async
    @Scheduled(cron = "${cron.clearing}")
    public void timeBasedClearing() {
        List<Pending> pendings = pendingRepository.findAllByCompletionDate();

        pendings.forEach(pending -> {
            ResponseClearingDto clearingDto = pendingMapper.toClearing(pending);
            clearingDto.setForced(false);

            kafkaProducerService.sendResponseClearing(clearingDto, requestTopic, partitionClearing);

            PendingResponseDto responseDto = statusStoragePendingResponseDto.saveStatus(clearingDto.getOperationId());
            updatePendingFromPendingResponse(pending, responseDto);
        });
    }

    public void cancelClearing(){
        //будет в v2.0))
    }

    private PendingResponseDto updatePendingFromPendingResponse(Pending pending, PendingResponseDto responseDto) {
        pending.setRequestStatus(responseDto.getRequestStatus());

        if (responseDto.getReason() != null) {
            pending.setReason(responseDto.getReason());
        }

        pendingRepository.save(pending);
        log.info("Pending is save with {} and {}", pending.getAccountNumber(), pending.getBalance());

        return responseDto;
    }
}
