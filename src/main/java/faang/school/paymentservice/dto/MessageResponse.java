package faang.school.paymentservice.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import faang.school.paymentservice.enums.TransferStatus;
import lombok.Builder;

import java.util.UUID;

@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public record MessageResponse(
        UUID paymentId,
        TransferStatus result
) {
}
