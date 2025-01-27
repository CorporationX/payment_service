package faang.school.paymentservice.dto.order;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateOrderDto {
        @NotNull
        private ServiceType serviceType;
        @NotBlank
        private String plan;
        @NotBlank
        private String paymentMethod;
        @NotNull
        @Positive
        private Long userId;
}
