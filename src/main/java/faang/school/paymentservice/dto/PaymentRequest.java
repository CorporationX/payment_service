package faang.school.paymentservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.math.BigDecimal;

@Schema(description = "Запрос для проведения платежа")
@Builder
public record PaymentRequest(
        @Schema(description = "Номер платежа", example = "123456789")
        @NotNull
        long paymentNumber,

        @Schema(description = "Сумма платежа", example = "100.50")
        @Min(1)
        @NotNull
        BigDecimal amount,

        @Schema(description = "Валюта платежа")
        @NotNull
        Currency currency
) {
}
