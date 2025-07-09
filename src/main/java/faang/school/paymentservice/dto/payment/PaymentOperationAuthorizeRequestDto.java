package faang.school.paymentservice.dto.payment;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record PaymentOperationAuthorizeRequestDto(@NotNull UUID accountFromId,
                                                  @NotNull UUID accountToId,
                                                  @NotNull UUID currencyId,
                                                  // TODO
                                         /*@DecimalMin(value = DecimalMin.List) */BigDecimal amount) {
}
