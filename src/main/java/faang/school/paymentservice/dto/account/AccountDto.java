package faang.school.paymentservice.dto.account;

import faang.school.paymentservice.dto.Currency;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AccountDto {
    @NotNull
    private Long id;
    @NotNull
    private String number;
    @NotNull
    private String accountType;
    @NotNull
    private Currency currency;
    @NotNull
    private String status;
    @NotNull
    private Integer version;
}
