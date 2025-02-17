package faang.school.paymentservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Информация об ошибке")
public record ErrorResponse(
        @Schema(description = "Сообщение об ошибке", example = "Некорректные данные")
        String message
) {
}
