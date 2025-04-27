package faang.school.paymentservice.service.currency;

import faang.school.paymentservice.dto.CurrencyDto;

import java.util.Arrays;
import java.util.stream.Collectors;

public class EnumConverter {
    public static String convertCurrencyEnumToString() {
        return Arrays.stream(CurrencyDto.values())
                .map(Enum::name)
                .collect(Collectors.joining(","));
    }
}
