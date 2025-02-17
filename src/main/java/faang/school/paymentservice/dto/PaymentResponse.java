package faang.school.paymentservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.io.Serializable;
import java.math.BigDecimal;

@Builder
@Schema(description = "Ответ на запрос платежа")
public record PaymentResponse(
        @Schema(description = "Статус платежа")
        PaymentStatus status,
        @Schema(description = "Код подтверждения платежа", example = "1234")
        int verificationCode,
        @Schema(description = "Номер платежа", example = "123456789")
        long paymentNumber,
        @Schema(description = "Сумма платежа", example = "100.50")
        BigDecimal amount,
        @Schema(description = "Валюта платежа")
        Currency currency,
        @Schema(description = "Сообщение о результате платежа", example = "Платеж принят")
        String message
) implements Serializable {
}
