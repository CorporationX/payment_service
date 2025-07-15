package faang.school.paymentservice.dto.pending;

import faang.school.paymentservice.enums.RequestStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestOpenDto {
    private RequestStatus status;
    private String operationId;
    private String reason;
}
