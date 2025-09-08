package faang.school.paymentservice.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PaymentMessageType {
    AUTHORIZATION("Авторизация"),
    CANCEL("Отмена"),
    CLEARING("Проведение платежа"),
    FAILED("Неудачный платеж");

    private final String description;
}