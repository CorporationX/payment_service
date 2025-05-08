package faang.school.paymentservice.dto;

import lombok.Builder;

@Builder
public record ErrorResponse(String message) {
}
