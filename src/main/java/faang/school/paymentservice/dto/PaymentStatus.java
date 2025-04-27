package faang.school.paymentservice.dto;

public enum PaymentStatus {
    PENDING,       // Платеж создан, но не обработан
    AUTHORIZED,    // Деньги заблокированы, но не списаны
    CLEARED,       // Средства окончательно переведены (технический статус)
    CANCELLED,     // Платеж отменен
    FAILED         // Ошибка при обработке
}
