package faang.school.paymentservice.dto;

public enum PaymentStatus {
    PENDING,       // Платеж создан, но не обработан
    AUTHORIZED,    // Деньги заблокированы, но не списаны
    CLEARED,       // Средства окончательно переведены (технический статус)
    SUCCESS,       // Платеж успешно завершен (финальный статус для пользователя)
    CANCELLED,     // Платеж отменен
    FAILED         // Ошибка при обработке
}
