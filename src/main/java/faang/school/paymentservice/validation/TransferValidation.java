package faang.school.paymentservice.validation;

import faang.school.paymentservice.dto.PaymentStatus;
import faang.school.paymentservice.dto.TransferStatus;
import faang.school.paymentservice.entity.Transfer;
import faang.school.paymentservice.exception.common.DataValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Slf4j
@Component
public class TransferValidation {

    public void validateTransferInitiator(Long userId, Transfer transfer) {
        if (!Objects.equals(transfer.getInitiatorId(), userId)) {
            log.error("Transfer with id {} does not belong to cancel initiator", transfer.getId());
            throw new DataValidationException(
                    String.format("Transfer with id %s does not belong to cancel initiator", transfer.getId())
            );
        }
    }

    public void validateCancelTransferEntityStatus(Transfer transfer) {
        if (transfer.getTransferStatus() == TransferStatus.CLOSED) {
            log.error("Transfer with id {} is closed. Any actions with this operation are prohibited.", transfer.getId());
            throw new DataValidationException(
                    String.format("Transfer with id %s is closed. Any actions with this operation are prohibited.", transfer.getId())
            );
        }
    }

    public void validateTransferAvailableForClearing(Transfer transfer) {
        if (transfer.getPaymentStatus() != PaymentStatus.AUTHORIZED) {
            log.error("Transfer with id {} is not authorized for clearing", transfer.getId());
            throw new DataValidationException(
                    String.format("Transfer with id %s is not authorized for clearing", transfer.getId())
            );
        }
    }
}
