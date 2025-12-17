package faang.school.paymentservice.mapper;

import faang.school.paymentservice.dto.AuthorizationDto;
import faang.school.paymentservice.dto.TransferDto;
import faang.school.paymentservice.model.PaymentStatus;
import faang.school.paymentservice.model.Transfer;

public class TransferMapper {

    public static TransferDto toTransferDto(Transfer transfer) {
        return new TransferDto(
                transfer.getSenderAccountId(),
                transfer.getRecipientAccountId(),
                transfer.getAmount(),
                transfer.getProductCategory(),
                transfer.getClearScheduledAt(),
                transfer.getStatus()
        );
    }

    public static Transfer toTransfer(AuthorizationDto dto) {
        return Transfer.builder()
                .senderAccountId(dto.senderAccountId())
                .recipientAccountId(dto.recipientAccountId())
                .amount(dto.amount())
                .productCategory(dto.productCategory())
                .status(PaymentStatus.ON_AUTHORIZATION)
                .build();
    }
}
