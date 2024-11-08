package faang.school.paymentservice.model.dto;

import faang.school.paymentservice.model.enums.Currency;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AccountDto {
    private Long id;
    private String number;
    private Long projectId;
    private Long userId;
//    private AccountType type;
    private Currency currency;
//    private AccountStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime closedAt;
    private Long balanceId;
}
