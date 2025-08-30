package faang.school.paymentservice.model.enums;

public enum PaymentMessageType {
    AUTHORIZATION,  // Сообщение авторизации (резервирование средств)
    CANCEL,         // Сообщение отмены платежа
    CLEARING        // Сообщение клиринга (фактическое списание)
}