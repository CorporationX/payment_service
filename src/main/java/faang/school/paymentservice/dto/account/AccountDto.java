package faang.school.paymentservice.dto.account;


import faang.school.paymentservice.model.Currency;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;


@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountDto {
    private UUID id;

    private String accountNumber;

    private Long externalId;

    private OwnerType ownerType;

    private AccountType accountType;

    private Currency currency;

    private AccountStatus accountStatus;

    public enum AccountStatus {
        ACTIVE,
        SUSPENDED,
        CLOSED
    }

    public enum AccountType {
        PERSONAL,
        CORPORATE
    }

    public enum OwnerType {
        USER,
        PROJECT
    }
}


