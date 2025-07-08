package faang.school.paymentservice.event;

import faang.school.paymentservice.dto.ClearEventInitiator;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class ClearingTransferEventRequest {
    private ClearEventInitiator initiator;
    private UUID accountEventId;
}
