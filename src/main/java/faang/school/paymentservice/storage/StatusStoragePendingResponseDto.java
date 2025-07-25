package faang.school.paymentservice.storage;

import faang.school.paymentservice.dto.pending.PendingResponseDto;
import faang.school.paymentservice.enums.RequestStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StatusStoragePendingResponseDto extends StatusStorage<PendingResponseDto> {

    public PendingResponseDto saveStatus(String operationId) {
        return awaitStatus(operationId, (exp, id) -> {
            return new PendingResponseDto(RequestStatus.FAILED, exp, id);
        });
    }
}
