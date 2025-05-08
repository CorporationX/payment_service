package faang.school.paymentservice.dto;

import faang.school.paymentservice.entity.OwnerType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record PaymentOperationRequest(

        @NotNull(message = "Sender id must not be null")
        Long senderId,

        @NotNull(message = "Sender type must not be null")
        OwnerType senderType,

        @NotNull(message = "Receiver id must not be null")
        Long receiverId,

        @NotNull(message = "Receiver type must not be null")
        OwnerType receiverType,

        @NotNull(message = "Amount must not be null")
        @DecimalMin(value = "1.00", message = "Amount must be greater or equal {value}")
        @Digits(integer = 13, fraction = 2,
                message = "amount must have at most {integer} integer digits and {fraction} decimal places")
        BigDecimal amount,

        @NotNull(message = "Currency must not be null")
        Currency currency
) {
}
