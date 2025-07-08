package faang.school.paymentservice.service.payment;

import faang.school.paymentservice.config.context.UserContext;
import faang.school.paymentservice.dto.PaymentRequest;
import faang.school.paymentservice.dto.transfer.TransferRequest;
import faang.school.paymentservice.dto.transfer.TransferResponse;
import faang.school.paymentservice.event.TransferEventRequest;
import faang.school.paymentservice.mapper.TransferRequestMapper;
import faang.school.paymentservice.publisher.CancelTransferEventPublisher;
import faang.school.paymentservice.publisher.ForceClearingEventPublisher;
import faang.school.paymentservice.publisher.TransferEventPublisher;
import faang.school.paymentservice.service.oxr.OxrService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService{

    private final UserContext userContext;
    private final TransferRequestMapper transferRequestMapper;
    private final OxrService oxrConfig;
    private static final BigDecimal PERCENT = BigDecimal.valueOf(0.99);

    private final TransferEventPublisher transferEventPublisher;
    private final CancelTransferEventPublisher cancelTransferEventPublisher;
    private final ForceClearingEventPublisher forceClearingEventPublisher;

    public TransferResponse startTransferAuthorization(TransferRequest dto) {
        Long userId = userContext.getUserId();

        TransferEventRequest eventRequest = transferRequestMapper.dtoToEvent(dto);

        eventRequest.setUserId(userId);
        eventRequest.setId(UUID.randomUUID());

        transferEventPublisher.publish(eventRequest);

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

}
