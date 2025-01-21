package faang.school.paymentservice.dto.order;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CreateOrderDto(
        @NotNull
        ServiceType serviceType,
        @NotBlank
        String plan,
        @NotBlank
        String paymentMethod,
        @NotNull
        @Positive
        Long userId
) {
}
