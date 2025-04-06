package faang.school.paymentservice.service.currency;

import faang.school.paymentservice.dto.Currency;

import java.util.Arrays;
import java.util.stream.Collectors;

public class EnumConverter {
    public static String convertCurrencyEnumToString() {
        return Arrays.stream(Currency.values())
                .map(Enum::name)
                .collect(Collectors.joining(","));
    }
}
