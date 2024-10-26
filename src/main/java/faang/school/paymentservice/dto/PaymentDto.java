package faang.school.paymentservice.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import faang.school.paymentservice.model.Currency;
import faang.school.paymentservice.model.PaymentStatus;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDto {

    private UUID id;

    @NotBlank(message = "'amount' can`t be null or empty")
    @Pattern(regexp = "^\\d+\\.\\d{2}$", message = "Amount should be a valid number with up to two decimal places")
    private String amount;

    @NotNull(message = "'currency' can`t be null")
    private Currency currency;

    @NotBlank(message = "'accountNumberFrom' can`t be null")
    @Size(min = 20, max = 20, message = "'accountNumberFrom' length should be equals 20")
    private String accountNumberFrom;

    @NotBlank(message = "'accountNumberTo' can`t be null")
    @Size(min = 20, max = 20, message = "'accountNumberTo' length should be equals 20")
    private String accountNumberTo;

    private PaymentStatus status;

    @NotNull(message = "'clearScheduledAt' can't be null")
    @Future(message = "'clearScheduledAt' should be in the future")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime clearScheduledAt;
}


