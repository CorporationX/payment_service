package faang.school.paymentservice.event;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Builder
@Getter
@Setter
public class CancelTransferEventRequest {
    private UUID accountEventId;
}
