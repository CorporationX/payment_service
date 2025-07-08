package faang.school.paymentservice.event;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class TransferSuccessEventResponse {
    private UUID id;
    private UUID accountEventId;
    private String description;
}
