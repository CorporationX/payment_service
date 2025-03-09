package faang.school.paymentservice.dto;

import faang.school.paymentservice.enums.Currency;
import faang.school.paymentservice.enums.PaymentType;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class PaymentRequest {

    @NotEmpty
    @Size(min = 12, max = 20)
    private String senderAccountNumber;

    @NotEmpty
    @Size(min = 12, max = 20)
    private String receiverAccountNumber;

    @Min(1)
    @NotNull
    private BigDecimal amount;

    @NotNull
    private Currency currency;

    @Future
    private LocalDateTime paymentDateTime;

    private PaymentType paymentType;
}