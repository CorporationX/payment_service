package faang.school.paymentservice.kafka;

import faang.school.paymentservice.model.dto.PaymentMessageDto;
import faang.school.paymentservice.model.enums.PaymentStages;
import faang.school.paymentservice.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Kafka listener для обработки сообщений о неудачных платежах.
 * При получении сообщения с FAILED статусом, обновляет соответствующий платеж в базе данных.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentFailedListener {

    private final PaymentRepository paymentRepository;

    /**
     * Обрабатывает сообщение о неудавшемся платеже.
     * Находит платеж по idempotencyToken и обновляет его статус на FAILED.
     *
     * @param message DTO с информацией о платеже
     */
    @KafkaListener(
            topics = "${app.kafka.topics.failed}",
            groupId = "payment-service-group",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void handleFailed(PaymentMessageDto message) {
        log.error("Получено сообщение FAILED: {}", message);

        paymentRepository.findByIdempotencyToken(message.getIdempotencyToken())
                .ifPresent(payment -> {
                    payment.setStatus(PaymentStages.FAILED);
                    payment.setUpdatedAt(LocalDateTime.now());
                    paymentRepository.save(payment);
                    log.info("Статус платежа обновлен на FAILED для idempotencyToken={}", message.getIdempotencyToken());
                });
    }
}