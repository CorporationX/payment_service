package faang.school.paymentservice.validator;

import faang.school.paymentservice.exception.InvalidOperationException;
import faang.school.paymentservice.model.OperationStatus;
import faang.school.paymentservice.model.PendingOperation;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
public class PendingOperationValidator {
    public void validateManualConfirmation(PendingOperation operation) {
        if (operation.getStatus() != OperationStatus.PENDING) {
            throw new InvalidOperationException("Operation cannot be manually confirmed as it is not in PENDING status");
        }
    }
    public void validateAutomaticConfirmation(PendingOperation operation) {
        if (operation.getStatus() != OperationStatus.PENDING) {
            throw new InvalidOperationException("Operation cannot be automatically confirmed as it is not in PENDING status");
        }
        if (operation.getClearScheduledAt().isAfter(LocalDateTime.now())) {
            throw new InvalidOperationException("Cannot automatically confirm operation before scheduled time");
        }
    }
}
