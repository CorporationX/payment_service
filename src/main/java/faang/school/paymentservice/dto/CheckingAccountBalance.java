package faang.school.paymentservice.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
@JsonIgnoreProperties(ignoreUnknown = true)
public class CheckingAccountBalance {
    private UUID operationId;
    private UUID sourceAccountId;
    private AccountBalanceStatus status;
}

