package faang.school.paymentservice.event.transfer;

import faang.school.paymentservice.dto.ClearEventInitiator;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Builder
@Getter
@Setter
public class ClearingTransferEventRequest {
    @NotNull
    private Long userId;
    @NotNull
    private ClearEventInitiator initiator;
    @NotNull
    private UUID transactionId;
}
