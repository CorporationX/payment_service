package faang.school.paymentservice.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import faang.school.paymentservice.enums.ClearingType;
import lombok.Builder;

import java.util.UUID;

@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public record ClearingMessageRequest(
        UUID paymentId,
        ClearingType clearingType
) {
}
