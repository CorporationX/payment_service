package faang.school.paymentservice.dto.payment;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record PaymentOperationAuthorizeRequestDto(@NotNull(message = "accountFromId is required") UUID accountFromId,
                                                  @NotNull(message = "accountToId is required") UUID accountToId,
                                                  @NotNull(message = "currencyId is required") UUID currencyId,
                                                  @NotNull(message = "amount is required")
                                                  @DecimalMin(value = "1", message = "Amount must be at least 1")
                                                  @DecimalMax(value = "10000000",
                                                          message = "Amount must be at most 10 000 000")
                                                  @Digits(integer = 8, fraction = 0,
                                                          message = "Amount must be a whole number")
                                                  BigDecimal amount) {
}
