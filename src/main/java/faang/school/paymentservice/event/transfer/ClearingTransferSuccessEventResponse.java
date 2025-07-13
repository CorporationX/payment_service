package faang.school.paymentservice.event.transfer;

import faang.school.paymentservice.dto.TransferStage;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class ClearingTransferSuccessEventResponse implements ClearTransferEventResponse {
    private UUID transactionId;
    private String description;
    private TransferStage transferStage;
}
