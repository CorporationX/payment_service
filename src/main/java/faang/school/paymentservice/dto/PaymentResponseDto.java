package faang.school.paymentservice.dto;

import faang.school.paymentservice.model.Currency;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
public class PaymentResponseDto {
    @NotNull
    private UUID paymentId;

    @NotBlank
    private String senderAccountNumber;

    @NotBlank
    private String receiverAccountNumber;

    @NotNull
    private BigDecimal amount;

    @NotNull
    private Currency currency;

    private String message;
}