package faang.school.paymentservice.dto.convert;

import faang.school.paymentservice.dto.Currency;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class ConvertDto {
    @NotNull(message = "Amount cannot be empty")
    @Positive(message = "Amount should be positive")
    BigDecimal amount;
    @NotBlank(message = "Currency cannot be empty")
    Currency fromCurrency;
    @NotBlank(message = "Currency cannot be empty")
    Currency toCurrency;
}