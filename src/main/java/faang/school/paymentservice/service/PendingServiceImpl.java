package faang.school.paymentservice.service;

import faang.school.paymentservice.client.WebClientForAccountService;
import faang.school.paymentservice.dto.PendingDto;
import faang.school.paymentservice.entity.Pending;
import faang.school.paymentservice.entity.PendingStatus;
import faang.school.paymentservice.mapper.PendingDtoMapper;
import faang.school.paymentservice.repository.PendingRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PendingServiceImpl implements PendingService {

    private final PendingDtoMapper mapper;
    private final PendingRepository pendingRepository;
    private final WebClientForAccountService webClient;

    @Override
    @Transactional
    public Long initAndSaving(PendingDto pendingDto, UUID uuid) {
        return pendingRepository.findByToken(uuid)
                .map(Pending::getId)
                .orElseGet(() -> {
                    Pending pending = mapper.toEntity(pendingDto);
                    pending.setToken(uuid);
                    pending.setStatus(PendingStatus.INITIALIZATION);
                    pending = pendingRepository.save(pending);
                    return pending.getId();
                });
    }

    @Override
    public PendingDto cancelPending(Long id, UUID token) {
        Pending pending = pendingRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("No pending found with %d".formatted(id)));

        String url = "/api/v1/payment/pending/cancel/" + pending.getId();

        return checkingIdempotency(pending, token, PendingStatus.CANCELED, url);
    }

    @Override
    public PendingDto forcedPaymentConfirmation(Long id, UUID token) {
        Pending pending = pendingRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("No pending found with %d".formatted(id)));

        String url = "/api/v1/payment/clearing/" + pending.getId();

        return checkingIdempotency(pending, token, PendingStatus.SUCCESS, url);
    }

    @Override
    public PendingDto getPending(Long id) {
        return mapper.toDto(pendingRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("No pending found with %d".formatted(id))));
    }

    private PendingDto checkingIdempotency(Pending pending, UUID token, PendingStatus pendingStatus, String url) {
        if (pending.getToken().equals(token)) {
            pending.setStatus(pendingStatus);
            return mapper.toDto(pending);
        } else {
            PendingDto pendingDto = mapper.toDto(pending);
            webClient.sendRequestCancelPending(pendingDto, url);
            return pendingDto;
        }
    }
}
