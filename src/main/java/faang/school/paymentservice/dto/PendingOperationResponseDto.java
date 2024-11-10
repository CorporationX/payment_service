package faang.school.paymentservice.dto;

import faang.school.paymentservice.model.AccountBalanceStatus;
import faang.school.paymentservice.model.OperationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PendingOperationResponseDto {
    private AccountBalanceStatus accountBalanceStatus;
    private OperationStatus status;
}
