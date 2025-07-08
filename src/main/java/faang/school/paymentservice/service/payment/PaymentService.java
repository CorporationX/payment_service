package faang.school.paymentservice.service.payment;

import faang.school.paymentservice.dto.transfer.CancelTransferRequest;
import faang.school.paymentservice.dto.transfer.ForceClearingTransferRequest;
import faang.school.paymentservice.dto.transfer.TransferRequest;
import faang.school.paymentservice.dto.transfer.TransferResponse;

import java.math.BigDecimal;

public interface PaymentService {
    BigDecimal convertCurrency(BigDecimal amount, String fromCurrency, String toCurrency);

    TransferResponse startTransferAuthorization(TransferRequest dto);

    TransferResponse cancelTransferAuthorization(CancelTransferRequest dto);

    TransferResponse forceTransferAuthorization(ForceClearingTransferRequest dto);
}
