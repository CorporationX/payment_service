package faang.school.paymentservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Список поддерживаемых валют")
public enum Currency {
    USD, EUR, RUB
}
