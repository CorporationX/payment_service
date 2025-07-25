package faang.school.paymentservice.kafka;

import faang.school.paymentservice.dto.pending.PendingRequestDto;
import faang.school.paymentservice.dto.pending.ResponseClearingDto;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaProducerService {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendPendingResponseDto(PendingRequestDto message, String topic, int partition) {
        kafkaTemplate.send(topic, partition, null, message);
    }

    public void sendResponseClearing(ResponseClearingDto clearingDto, String topic, int partition){
        kafkaTemplate.send(topic, partition, null, clearingDto);
    }
}
