package faang.school.paymentservice.event.transfer;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Builder
@Getter
@Setter
public class CancelTransferEventRequest {
    @NotNull
    private Long userId;
    @NotNull
    private UUID transactionId;
}
