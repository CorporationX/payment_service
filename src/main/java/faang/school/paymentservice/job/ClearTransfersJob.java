package faang.school.paymentservice.job;

import faang.school.paymentservice.config.transfer.ClearTransferConfig;
import faang.school.paymentservice.dto.ClearEventInitiator;
import faang.school.paymentservice.entity.Transfer;
import faang.school.paymentservice.event.transfer.ClearingTransferEventRequest;
import faang.school.paymentservice.publisher.ForceClearingEventPublisher;
import faang.school.paymentservice.service.payment.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.IntStream;

@RequiredArgsConstructor
@Component
@Slf4j
public class ClearTransfersJob {

    private final ThreadPoolTaskExecutor clearingTransferExecutor;
    private final ClearTransferConfig config;
    private final PaymentService service;
    private final ForceClearingEventPublisher forceClearingEventPublisher;

    @Scheduled(cron = "#{@clearTransferConfig.cron}")
    public void clearTransfersJob() {
        int totalTransfers = service.getNotClearedAtTimeTransfers();
        int batchSize = config.getBatchSize();
        int batchNumbers = (int) Math.ceil((double) totalTransfers / batchSize);

        log.info("Starting transaction clearing of {} batches with {} transactions in each", batchNumbers, batchSize);
        IntStream.range(0, batchNumbers).forEach((batchNumber) ->
                clearingTransferExecutor.submit(() -> publishCleaningRequests(batchSize))
        );
    }

    private void publishCleaningRequests(int batchSize) {
        List<Transfer> transfers = service.clearTransfer(batchSize);

        transfers.forEach((transfer) ->
            {
                ClearingTransferEventRequest request = ClearingTransferEventRequest.builder()
                    .userId(transfer.getInitiatorId())
                    .initiator(ClearEventInitiator.SYSTEM)
                    .transactionId(transfer.getAccountEventId())
                    .build();

                forceClearingEventPublisher.publish(request);
            }
        );
    }
}
