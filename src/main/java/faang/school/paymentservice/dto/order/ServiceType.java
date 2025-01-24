package faang.school.paymentservice.dto.order;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum ServiceType {
    PREMIUM, PROMOTION;

    @JsonCreator
    public static ServiceType fromString(String value) {
        return ServiceType.valueOf(value.toUpperCase());
    }
}
