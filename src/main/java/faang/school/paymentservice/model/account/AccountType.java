package faang.school.paymentservice.model.account;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum AccountType {
    DEBIT("4200"),
    SAVINGS("5236");

    private final String prefix;

    public static AccountType fromString(String value) {
        return Arrays.stream(values())
                .filter(t -> t.name().equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown account type: " + value));
    }
}
