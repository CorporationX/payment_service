package faang.school.paymentservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;

import java.io.Serializable;

@Schema(description = "Запрос для проведения рекламной акции")
@Builder
public record PromotionRequest(
        @Schema(description = "ID пользователя", example = "1")
        @NotNull @Positive
        Long userId,

        @Schema(description = "Бюджет на день", example = "200")
        @NotNull @Min(value = 100, message = "Бюджет на день не может быть меньше 100")
        Long budgetInDay,

        @Schema(description = "Количество дней", example = "7")
        @NotNull @Positive(message = "Количество дней не может быть меньше 1")
        Long countDays
) implements Serializable {
}
