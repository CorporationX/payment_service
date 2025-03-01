package faang.school.paymentservice.dto.account;

import faang.school.paymentservice.dto.Currency;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountCreateDto {
    private Long userId;
    private Long projectId;
    private Bank bank;
    private Currency currency;
    private AccountType type;
}
