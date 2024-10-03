package faang.school.paymentservice.dto;

public enum Currency {
    USD("USD"), EUR("EUR");
    private final String curName;
    Currency(String cur) {
        curName = cur;
    }
    public String getName() {
        return curName;
    }
}
