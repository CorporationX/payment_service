package faang.school.paymentservice.model.dto;

import faang.school.paymentservice.model.enums.Currency;
import faang.school.paymentservice.model.enums.PaymentStatus;
import jakarta.persistence.Version;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data

public class PendingOperationDto {
    @NotNull(message = "Must be generated on frontend")
    private UUID id;

    @NotNull
    private Long senderAccountId;

    @NotNull(message = "Receiver account id cannot be null")
    private Long receiverAccountId;

    @NotNull
    private Currency senderCurrency;

    @NotNull
    private Currency receiverCurrency;

    @NotNull
    @Positive(message = "Amount in sender currency cannot be null and must be positive")
    private BigDecimal amountInSenderCurrency;

    private BigDecimal amountInReceiverCurrency;

    @Null(message = "Status must be null")
    private PaymentStatus status;

    @Null(message = "Clear scheduletAt must be null")
    private LocalDateTime clearScheduledAt;

    @Null(message = "CreatedAt must be null")
    private LocalDateTime createdAt;

    @Null(message = "UpdatedAt must be null")
    private LocalDateTime updatedAt;

    @Version
    private Integer version;
}
