package faang.school.paymentservice.validation;

import faang.school.paymentservice.dto.TransferStage;
import faang.school.paymentservice.entity.Transfer;
import faang.school.paymentservice.exception.common.DataValidationException;
import faang.school.paymentservice.exception.common.PreConditionFailedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Slf4j
@Component
public class TransferValidation {

    public void validateNoActiveTransactions(Long activeTransactions, Long userId) {
        if (activeTransactions > 0) {
            log.error("User with id {} already has active transaction", userId);
            throw new PreConditionFailedException(
                    String.format("User with id %s already has active transaction", userId)
            );
        }
    }

    public void validateTransferInitiator(Long userId, Transfer transfer) {
        if (!Objects.equals(transfer.getInitiatorId(), userId)) {
            log.error("Transfer with id {} does not belong to cancel initiator", transfer.getId());
            throw new DataValidationException(
                    String.format("Transfer with id %s does not belong to cancel initiator", transfer.getId())
            );
        }
    }

    public void validateTransferAuthorized(Transfer transfer) {
        if (transfer.getTransferStage() != TransferStage.AUTHORIZED) {
            log.error("Transfer with id {} is not authorized.", transfer.getId());
            throw new DataValidationException(
                    String.format("Transfer with id %s is not authorized.", transfer.getId())
            );
        }
    }
}
