package faang.school.paymentservice.service;

import faang.school.paymentservice.dto.PendingDto;

import java.util.UUID;

public interface PendingService {

    Long initAndSaving(PendingDto pendingDto, UUID uuid);

    PendingDto cancelPending(Long id, UUID uuid);

    PendingDto forcedPaymentConfirmation(Long id, UUID uuid);

    PendingDto getPending(Long id);
}
