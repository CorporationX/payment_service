package faang.school.paymentservice.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;

import java.io.Serializable;

@Builder
public record PromotionRequest(@NotNull @Positive
                               Long userId,
                               @NotNull @Min(value = 100, message = "Бюджет на день не может быть меньше 100")
                               Long budgetInDay,
                               @NotNull @Positive(message = "Количество дней не может быть меньше 1")
                               Long countDays) implements Serializable {
}
