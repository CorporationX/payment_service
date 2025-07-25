package faang.school.paymentservice.dto.pending;

import faang.school.paymentservice.dto.Currency;
import faang.school.paymentservice.enums.RequestType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

public record PendingDto(
        @NotBlank @NotNull String accountNumber,
        @NotNull Long recipientId,
        @NotNull @DecimalMin(value = "0.01", message = "The amount must not be less than or equal to 0")
        BigDecimal balance,
        @NotNull RequestType requestType,
        @NotNull Currency currency,
        @NotBlank @NotNull String token,
        LocalDateTime completionDate,
        Map<String, String> requestInputData,
        String additionalDetails
) {
}
