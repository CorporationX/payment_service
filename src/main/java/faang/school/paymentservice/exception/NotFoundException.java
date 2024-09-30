package faang.school.paymentservice.exception;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }
}
