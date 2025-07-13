package faang.school.paymentservice.model.payment;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OperationTokenModel {
    private UUID key;
    private LocalDateTime createdAt;
    private boolean wasAlreadyPresent;
}
