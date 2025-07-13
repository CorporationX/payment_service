package faang.school.paymentservice.event.transfer;

import faang.school.paymentservice.dto.TransferStage;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class TransferFailEventResponse implements TransferEventResponse {
    private UUID authorizationId;
    private UUID transactionId;
    private String description;
    private TransferStage transferStage;
}
