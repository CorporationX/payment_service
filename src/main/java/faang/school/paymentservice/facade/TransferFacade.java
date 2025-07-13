package faang.school.paymentservice.facade;

import faang.school.paymentservice.dto.ClearEventInitiator;
import faang.school.paymentservice.dto.transfer.CancelTransferRequest;
import faang.school.paymentservice.dto.transfer.ForceClearingTransferRequest;
import faang.school.paymentservice.dto.transfer.TransferRequest;
import faang.school.paymentservice.dto.transfer.TransferResponse;
import faang.school.paymentservice.entity.Transfer;
import faang.school.paymentservice.event.transfer.CancelTransferEventRequest;
import faang.school.paymentservice.event.transfer.ClearingTransferEventRequest;
import faang.school.paymentservice.event.transfer.TransferEventRequest;
import faang.school.paymentservice.mapper.TransferRequestMapper;
import faang.school.paymentservice.publisher.CancelTransferEventPublisher;
import faang.school.paymentservice.publisher.ForceClearingEventPublisher;
import faang.school.paymentservice.publisher.TransferEventPublisher;
import faang.school.paymentservice.service.payment.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TransferFacade {

    private final PaymentService paymentService;
    private final TransferRequestMapper transferRequestMapper;

    private final TransferEventPublisher transferEventPublisher;
    private final CancelTransferEventPublisher cancelTransferEventPublisher;
    private final ForceClearingEventPublisher forceClearingEventPublisher;

    public TransferResponse startTransferAuthorization(TransferRequest dto) {

        Transfer savedTransfer = paymentService.startTransferAuthorization(dto);

        TransferEventRequest eventRequest = transferRequestMapper.dtoToEvent(dto);
        eventRequest.setUserId(savedTransfer.getInitiatorId());
        eventRequest.setAuthorizationId(savedTransfer.getId());

        transferEventPublisher.publish(eventRequest);

        return TransferResponse.builder()
                .transferStage(savedTransfer.getTransferStage())
                .transferId(savedTransfer.getId())
                .transferStatus(savedTransfer.getTransferStatus())
                .build();
    }

    public TransferResponse cancelTransfer(CancelTransferRequest dto) {

        Transfer transfer = paymentService.cancelTransfer(dto);

        CancelTransferEventRequest request = CancelTransferEventRequest.builder()
                .userId(transfer.getInitiatorId())
                .transactionId(dto.transferId())
                .build();

        cancelTransferEventPublisher.publish(request);

        return TransferResponse.builder()
                .transferStage(transfer.getTransferStage())
                .transferId(transfer.getId())
                .transferStatus(transfer.getTransferStatus())
                .build();
    }

    public TransferResponse forceTransferClearing(ForceClearingTransferRequest dto) {

        Transfer transfer = paymentService.forceTransferClearing(dto);

        ClearingTransferEventRequest request = ClearingTransferEventRequest.builder()
                .userId(transfer.getInitiatorId())
                .initiator(ClearEventInitiator.USER)
                .transactionId(transfer.getAccountEventId())
                .build();

        forceClearingEventPublisher.publish(request);

        return TransferResponse.builder()
                .transferStage(transfer.getTransferStage())
                .transferId(transfer.getId())
                .transferStatus(transfer.getTransferStatus())
                .build();
    }
}
