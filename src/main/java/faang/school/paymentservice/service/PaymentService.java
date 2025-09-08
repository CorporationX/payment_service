package faang.school.paymentservice.service;

import faang.school.paymentservice.model.dto.PaymentRequestDto;
import faang.school.paymentservice.model.dto.PaymentResponseDto;

import java.util.UUID;

public interface PaymentService {

    /**
     * Инициация нового платежа.
     * Сохраняет запись в БД и отправляет Kafka-сообщение Authorization.
     * Проверяет идемпотентность по requestId.
     *
     * @param request данные платежа
     * @return информация о платеже (requestId, статус, суммы)
     */
    PaymentResponseDto initiatePayment(PaymentRequestDto request);

    /**
     * Отмена платежа.
     * Обновляет статус платежа и отправляет Kafka-сообщение Cancel в account_service.
     *
     * @param requestId идентификатор платежа
     * @return информация о платеже после отмены
     */
    PaymentResponseDto cancelPayment(UUID requestId);

    /**
     * Принудительное подтверждение платежа.
     * Обновляет статус платежа и отправляет Kafka-сообщение Clearing в account_service.
     *
     * @param requestId идентификатор платежа
     * @return информация о платеже после подтверждения
     */
    PaymentResponseDto confirmPayment(UUID requestId);

    /**
     * Получение информации о платеже по requestId.
     *
     * @param requestId идентификатор платежа
     * @return информация о платеже
     */
    PaymentResponseDto getPaymentByIdempotencyToken(UUID requestId);
}