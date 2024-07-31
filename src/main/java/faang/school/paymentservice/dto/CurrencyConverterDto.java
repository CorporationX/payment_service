package faang.school.paymentservice.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CurrencyConverterDto {
    @NotNull(message = "Валюта, из которой совершается конвертация, не может быть пустой")
    Currency transmittedCurrency;
    @NotNull(message = "Сумма, из которой совершается конвертация, не может быть пустой")
    BigDecimal transmittedSum;
    @NotNull(message = "Валюта, в которую совершается конвертация, не может быть пустой")
    Currency receivedCurrency;
    BigDecimal receivedSum;
    BigDecimal commission;
    BigDecimal totalSum;
}
