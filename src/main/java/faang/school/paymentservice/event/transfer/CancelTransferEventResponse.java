package faang.school.paymentservice.event.transfer;

import faang.school.paymentservice.dto.TransferStage;

import java.util.UUID;

public interface CancelTransferEventResponse {
    UUID getTransactionId();

    TransferStage getTransferStage();

    String getDescription();
}
