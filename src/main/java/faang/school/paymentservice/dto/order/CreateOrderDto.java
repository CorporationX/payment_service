package faang.school.paymentservice.dto.order;

import jakarta.validation.constraints.NotBlank;

public record CreateOrderDto(
        @NotBlank
        String serviceType,
        @NotBlank
        String plan,
        @NotBlank
        String paymentMethod
) {
}
