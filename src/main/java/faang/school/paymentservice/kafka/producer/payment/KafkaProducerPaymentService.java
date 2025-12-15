package faang.school.paymentservice.kafka.producer.payment;

import faang.school.paymentservice.kafka.dto.AuthorizationKafkaRequestDto;
import faang.school.paymentservice.kafka.dto.CancelKafkaRequestDto;
import faang.school.paymentservice.kafka.dto.ClearingKafkaRequestDto;
import faang.school.paymentservice.kafka.producer.KafkaProducerService;
import faang.school.paymentservice.model.Transfer;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class KafkaProducerPaymentService {

    private final KafkaProducerService kafkaProducerService;

    @Value("${spring.kafka.topics.payment.authorization-request}")
    private String authorizationRequestTopic;

    @Value("${spring.kafka.topics.payment.clearing-request}")
    private String clearingRequestTopic;

    @Value("${spring.kafka.topics.payment.cancel-request}")
    private String cancelRequestTopic;

    public void sendAuthorizationRequest(Transfer transfer) {
        AuthorizationKafkaRequestDto paymentToSend = new AuthorizationKafkaRequestDto(
                transfer.getSenderAccountId(),
                transfer.getAmount(),
                transfer.getId());
        kafkaProducerService.sendMessage(authorizationRequestTopic, paymentToSend);
    }

    public void sendClearingRequest(Transfer transfer) {
        ClearingKafkaRequestDto paymentToSend = new ClearingKafkaRequestDto(
                transfer.getSenderAccountId(),
                transfer.getRecipientAccountId(),
                transfer.getAmount(),
                transfer.getId());
        kafkaProducerService.sendMessage(clearingRequestTopic, paymentToSend);
    }

    public void sendCancelRequest(Transfer transfer) {
        CancelKafkaRequestDto paymentToSend = new CancelKafkaRequestDto(
                transfer.getSenderAccountId(),
                transfer.getAmount(),
                transfer.getId());
        kafkaProducerService.sendMessage(cancelRequestTopic, paymentToSend);
    }
}
