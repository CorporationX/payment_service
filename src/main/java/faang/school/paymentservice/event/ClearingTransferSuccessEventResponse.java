package faang.school.paymentservice.event;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class ClearingTransferSuccessEventResponse {
    private UUID accountEventId;
    private String description;
}
