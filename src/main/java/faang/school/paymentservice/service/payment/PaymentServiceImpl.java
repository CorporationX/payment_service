package faang.school.paymentservice.service.payment;

import faang.school.paymentservice.config.context.UserContext;
import faang.school.paymentservice.dto.TransferStage;
import faang.school.paymentservice.dto.TransferStatus;
import faang.school.paymentservice.dto.transfer.CancelTransferRequest;
import faang.school.paymentservice.dto.transfer.ForceClearingTransferRequest;
import faang.school.paymentservice.dto.transfer.TransferRequest;
import faang.school.paymentservice.entity.Transfer;
import faang.school.paymentservice.event.transfer.CancelTransferEventResponse;
import faang.school.paymentservice.event.transfer.ClearTransferEventResponse;
import faang.school.paymentservice.event.transfer.TransferEventResponse;
import faang.school.paymentservice.exception.common.RecordNotFoundException;
import faang.school.paymentservice.mapper.TransferRequestMapper;
import faang.school.paymentservice.repository.TransferRepository;
import faang.school.paymentservice.service.oxr.OxrService;
import faang.school.paymentservice.validation.TransferValidation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService{

    private final UserContext userContext;
    private final TransferRequestMapper transferRequestMapper;
    private final TransferRepository transferRepository;
    private final TransferValidation transferValidation;

    private final OxrService oxrConfig;
    private static final BigDecimal PERCENT = BigDecimal.valueOf(0.99);

    @Transactional
    public Transfer startTransferAuthorization(TransferRequest dto) {
        Long userId = userContext.getUserId();
        Long activeTransactions = transferRepository.countUserActiveTransactions(userId);

        transferValidation.validateNoActiveTransactions(activeTransactions, userId);

        Transfer transfer = transferRequestMapper.dtoToEntity(dto);
        transfer.setInitiatorId(userId);

        return transferRepository.save(transfer);
    }

    @Transactional
    public Transfer cancelTransfer(CancelTransferRequest dto) {
        Long userId = userContext.getUserId();
        Transfer transfer = transferRepository.findByAccountEventId(dto.transferId());

        transferValidation.validateTransferInitiator(userId, transfer);
        transferValidation.validateTransferAuthorized(transfer);

        transfer.setTransferStage(TransferStage.CANCELLATION_PENDING);
        transfer.setUpdatedAt(LocalDateTime.now());

        return transferRepository.save(transfer);
    }

    @Transactional
    public Transfer forceTransferClearing(ForceClearingTransferRequest dto) {
        Long userId = userContext.getUserId();
        Transfer transfer = transferRepository.findByAccountEventId(dto.transferId());

        transferValidation.validateTransferInitiator(userId, transfer);
        transferValidation.validateTransferAuthorized(transfer);

        transfer.setTransferStage(TransferStage.CLEARING_PENDING);
        transfer.setUpdatedAt(LocalDateTime.now());

        return transferRepository.save(transfer);
    }

    @Transactional
    public void handleTransferEvent(TransferEventResponse event) {
        Transfer transfer = getValidTransfer(event.getAuthorizationId());
        transfer.setTransferStage(event.getTransferStage());
        transfer.setDescription(event.getDescription());
        transfer.setAccountEventId(event.getTransactionId());
        transfer.setClearedAt(LocalDateTime.now().plusMinutes(5));
        transfer.setUpdatedAt(LocalDateTime.now());

        transferRepository.save(transfer);
    }

    @Transactional
    public void handleCancelTransferEvent(CancelTransferEventResponse event) {
        Transfer transfer = transferRepository.findByAccountEventId(event.getTransactionId());
        transfer.setTransferStage(event.getTransferStage());
        transfer.setDescription(event.getDescription());
        transfer.setUpdatedAt(LocalDateTime.now());

        if (event.getTransferStage() == TransferStage.CANCELED) {
            transfer.setTransferStatus(TransferStatus.CLOSED);
        }

        transferRepository.save(transfer);
    }

    @Transactional
    public void handleClearTransferEvent(ClearTransferEventResponse event) {
        Transfer transfer = transferRepository.findByAccountEventId(event.getTransactionId());
        transfer.setTransferStage(event.getTransferStage());
        transfer.setDescription(event.getDescription());
        transfer.setUpdatedAt(LocalDateTime.now());

        if (event.getTransferStage() == TransferStage.CLEARED) {
            transfer.setTransferStatus(TransferStatus.CLOSED);
        }

        transferRepository.save(transfer);
    }

    @Override
    public BigDecimal convertCurrency(BigDecimal amount, String fromCurrency, String toCurrency) {
        BigDecimal fromRate = receivingCurrency(fromCurrency);
        BigDecimal toRate = receivingCurrency(toCurrency);
        BigDecimal result = toRate.divide(fromRate, 10, RoundingMode.HALF_UP).multiply(PERCENT);
        return amount.multiply(result).setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal receivingCurrency(String currency) {
        return BigDecimal.valueOf(oxrConfig.getLatestRates().getRates().entrySet()
                .stream()
                .filter(rate -> rate.getKey().equals(currency))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Currency Not Found " + currency))
                .getValue());
    }

    public Transfer getValidTransfer(UUID transferId) {
        return transferRepository.findById(transferId)
                .orElseThrow(() -> new RecordNotFoundException(
                        String.format("There is no transfer with id %s", transferId)
                ));
    }

    @Transactional
    public List<Transfer> getDueTransfers() {
        return transferRepository.getDueTransfers();
    }
}
