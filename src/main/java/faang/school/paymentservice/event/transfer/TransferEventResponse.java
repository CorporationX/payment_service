package faang.school.paymentservice.event.transfer;

import faang.school.paymentservice.dto.TransferStage;

import java.util.UUID;

public interface TransferEventResponse {
    UUID getAuthorizationId();

    TransferStage getTransferStage();

    String getDescription();

    UUID getTransactionId();
}
