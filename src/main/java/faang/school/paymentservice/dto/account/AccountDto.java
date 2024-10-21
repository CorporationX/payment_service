package faang.school.paymentservice.dto.account;

import faang.school.paymentservice.model.Currency;
import faang.school.paymentservice.model.account.AccountStatus;
import faang.school.paymentservice.model.account.AccountType;
import faang.school.paymentservice.model.owner.OwnerType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountDto {
    private Long id;

    private String accountNumber;

    @NotNull(message = "\"externalOwnerId\" cannot be NULL")
    private Long externalId;

    @NotNull(message = "\"ownerType\" cannot be NULL")
    private OwnerType ownerType;

    @NotNull(message = "\"accountType\" cannot be NULL")
    private AccountType accountType;

    @NotNull(message = "\"currency\" cannot be NULL")
    private Currency currency;

    private AccountStatus accountStatus;
}
