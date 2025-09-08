package faang.school.paymentservice.model.dto;

import faang.school.paymentservice.dto.Currency;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequestDto {

    /**
     * Счёт отправителя платежа
     */
    @NotNull(message = "fromAccountId не должен быть null")
    private Long fromAccountId;

    /**
     * Счёт получателя платежа
     */
    @NotNull(message = "toAccountId не должен быть null")
    private Long toAccountId;

    /**
     * Сумма платежа, должна быть больше 0
     */
    @NotNull(message = "amount не должен быть null")
    @DecimalMin(value = "0.01", message = "Сумма должна быть больше нуля")
    private BigDecimal amount;

    /**
     * Валюта платежа
     */
    @NotNull(message = "currency не должен быть null")
    private Currency currency;

    /**
     * Время планируемого клиринга
     */
    private LocalDateTime clearScheduledAt;
}