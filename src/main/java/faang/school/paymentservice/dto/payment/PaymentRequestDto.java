package faang.school.paymentservice.dto.payment;

import com.fasterxml.jackson.annotation.JsonFormat;
import faang.school.paymentservice.model.Currency;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequestDto {

    @NotBlank(message = "'amount' can`t be null or empty")
    @Pattern(regexp = "^\\d+\\.\\d{2}$", message = "'amount' should be a positive number with up to two decimal places")
    private String amount;

    @NotNull(message = "'currency' can`t be null")
    private Currency currency;

    @NotBlank(message = "'accountNumberFrom' can`t be null")
    @Pattern(regexp = "\\d{20}", message = "'accountNumberFrom' must be 20 digits long")
    private String accountNumberFrom;

    @NotBlank(message = "'accountNumberTo' can`t be null")
    @Pattern(regexp = "\\d{20}", message = "'accountNumberTo' must be 20 digits long")
    private String accountNumberTo;

    @NotNull(message = "'clearScheduledAt' can't be null")
    @Future(message = "'clearScheduledAt' should be in the future")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime clearScheduledAt;
}


