package faang.school.paymentservice.dto;

import java.util.Arrays;
import java.util.stream.Collectors;

public enum Currency {
    USD, EUR;

    public static String getCurrenciesAsString() {
        return Arrays.stream(Currency.values())
                .map(Enum::name)
                .collect(Collectors.joining(","));
    }
}
