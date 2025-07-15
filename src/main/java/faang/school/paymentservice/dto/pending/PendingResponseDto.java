package faang.school.paymentservice.dto.pending;

import faang.school.paymentservice.enums.RequestStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PendingResponseDto {
    private RequestStatus requestStatus;
    private String reason;
    private String operationId;
}
