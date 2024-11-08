package faang.school.paymentservice.model.dto;

import faang.school.paymentservice.model.enums.PaymentStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data

public class PaymentDto {
    @Null(groups = {Create.class}, message = "Id must be null")
    private Long id;

    @NotNull(groups = {Create.class}, message = "Receiver account number cannot be null")
    private String receiverAccountNumber;

    @NotNull
    @NotBlank(groups = {Create.class}, message = "Owner account number cannot be null")
    private String ownerAccountNumber;

    @NotNull
    private UUID idempotencyToken;

    @NotNull
    @NotBlank(groups = {Create.class}, message = "Currency cannot be null")
    private String currency;

    @NotNull
    @Positive(groups = {Create.class}, message = "Amount cannot be null and must be positive")
    private BigDecimal amount;

    @Null(groups = {Create.class}, message = "Status must be null")
    private PaymentStatus status;

    @Null(groups = {Create.class}, message = "Clear scheduletAt must be null")
    private LocalDateTime clearScheduledAt;

    @Null(groups = {Create.class}, message = "CreatedAt must be null")
    private LocalDateTime createdAt;

    @Null(groups = {Create.class}, message = "UpdatedAt must be null")
    private LocalDateTime updatedAt;

    public interface Create {

    }
}
