package faang.school.paymentservice.service;

import faang.school.paymentservice.model.dto.PaymentDto;
import faang.school.paymentservice.model.dto.PendingOperationDto;

import java.util.UUID;

public interface PendingOperationService {
    UUID createPayment(PendingOperationDto pendingOperationDto);
    void cancelPayment(UUID pendingOperationId);
    PendingOperationDto getPendingOperation(UUID pendingOperationId);
}
