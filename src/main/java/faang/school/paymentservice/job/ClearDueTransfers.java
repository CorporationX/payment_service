package faang.school.paymentservice.job;

import faang.school.paymentservice.config.transfer.ClearDueTransferConfig;
import faang.school.paymentservice.dto.ClearEventInitiator;
import faang.school.paymentservice.entity.Transfer;
import faang.school.paymentservice.event.transfer.ClearingTransferEventRequest;
import faang.school.paymentservice.publisher.ForceClearingEventPublisher;
import faang.school.paymentservice.service.payment.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Component
@Slf4j
public class ClearDueTransfers {

    private final ThreadPoolTaskExecutor clearingTransferExecutor;
    private final ClearDueTransferConfig config;
    private final PaymentService service;
    private final ForceClearingEventPublisher forceClearingEventPublisher;

    @Scheduled(cron = "#{@clearDueTransferConfig.cron}")
    @Transactional
    public void clearTransfersJob() {
        List<List<Transfer>> batchedTransfers = getDueTransfersPartitions();

        batchedTransfers.forEach(batch -> clearingTransferExecutor.submit(() -> clearTransfers(batch)));
    }

    private void clearTransfers(List<Transfer> transfers) {
        log.info("Clearing started. Batch size: {}", transfers.size());

        transfers.forEach(transfer -> {
            ClearingTransferEventRequest request = ClearingTransferEventRequest.builder()
                    .userId(transfer.getInitiatorId())
                    .initiator(ClearEventInitiator.USER)
                    .transactionId(transfer.getAccountEventId())
                    .build();

            forceClearingEventPublisher.publish(request);
        });

        log.info("Finished sending {} clearing requests to kafka.", transfers.size());
    }

    private List<List<Transfer>> getDueTransfersPartitions() {
        List<Transfer> transfers = service.getDueTransfers();
        int batchSize = config.getBatchSize();

        return ListUtils.partition(transfers, batchSize);
    }
}
