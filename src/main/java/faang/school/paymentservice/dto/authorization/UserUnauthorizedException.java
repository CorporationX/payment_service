package faang.school.paymentservice.dto.authorization;

public class UserUnauthorizedException extends RuntimeException {
    public UserUnauthorizedException(String msg) {
        super(msg);
    }
}

