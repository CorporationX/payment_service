package faang.school.paymentservice.dto;

import faang.school.paymentservice.model.AccountBalanceStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CheckingAccountBalance {
    private UUID operationId;
    private UUID accountFromId;
    private AccountBalanceStatus status;
}

