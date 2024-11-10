package faang.school.paymentservice.dto.payment;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentUpdateDto {

    @NotNull(message = "action is required")
    private PaymentUpdateAction action;
}