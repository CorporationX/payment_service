package faang.school.paymentservice.service.payment;

import faang.school.paymentservice.dto.transfer.CancelTransferRequest;
import faang.school.paymentservice.dto.transfer.ForceClearingTransferRequest;
import faang.school.paymentservice.dto.transfer.TransferRequest;
import faang.school.paymentservice.entity.Transfer;
import faang.school.paymentservice.event.transfer.CancelTransferEventResponse;
import faang.school.paymentservice.event.transfer.ClearTransferEventResponse;
import faang.school.paymentservice.event.transfer.TransferEventResponse;

import java.math.BigDecimal;
import java.util.List;

public interface PaymentService {
    BigDecimal convertCurrency(BigDecimal amount, String fromCurrency, String toCurrency);

    Transfer startTransferAuthorization(TransferRequest dto);

    Transfer cancelTransfer(CancelTransferRequest dto);

    Transfer forceTransferClearing(ForceClearingTransferRequest dto);

    void handleTransferEvent(TransferEventResponse event);

    void handleCancelTransferEvent(CancelTransferEventResponse event);

    void handleClearTransferEvent(ClearTransferEventResponse event);

    List<Transfer> getDueTransfers();
}
