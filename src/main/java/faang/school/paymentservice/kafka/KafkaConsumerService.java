package faang.school.paymentservice.kafka;

import faang.school.paymentservice.dto.pending.PendingResponseDto;
import faang.school.paymentservice.dto.pending.RequestOpenDto;
import faang.school.paymentservice.service.PendingService;
import faang.school.paymentservice.storage.StatusStoragePendingResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.TopicPartition;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaConsumerService {

    private final StatusStoragePendingResponseDto statusStoragePendingResponseDto;
    private final PendingService pendingService;

    @KafkaListener(
            topicPartitions = @TopicPartition(
                    topic = "${kafka.topics.pending-topic}",
                    partitions = "${kafka.partitions.partition-status}"
            ),
            groupId = "${kafka.groups.group1}",
            containerFactory = "pendingResponseKafkaListenerContainerFactory"
    )
    public void listenerPendingResponse(@Valid PendingResponseDto responseDto){
        statusStoragePendingResponseDto.updateResponse(responseDto, responseDto.getOperationId());
    }

    @KafkaListener(
            topicPartitions = @TopicPartition(
                    topic = "${kafka.topics.pending-topic}",
                    partitions = "${kafka.partitions.partition-open-request}"
            ),
            groupId = "${kafka.groups.group1}",
            containerFactory = "requestOpenKafkaListenerContainerFactory"
    )
    public void listenerOpenRequest(@Valid RequestOpenDto openDto){
        pendingService.openRequest(openDto);
    }

    @KafkaListener(
            topicPartitions = @TopicPartition(
                    topic = "${kafka.topics.pending-topic}",
                    partitions = "${kafka.partitions.partition-clearing-response}"
            ),
            groupId = "${kafka.groups.group1}",
            containerFactory = "pendingResponseKafkaListenerContainerFactory"
    )
    public void listenerClearingResponse(PendingResponseDto responseDto){
        statusStoragePendingResponseDto.updateResponse(responseDto, responseDto.getOperationId());
    }
}
