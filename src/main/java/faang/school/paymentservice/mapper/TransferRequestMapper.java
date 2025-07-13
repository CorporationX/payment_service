package faang.school.paymentservice.mapper;

import faang.school.paymentservice.dto.transfer.TransferRequest;
import faang.school.paymentservice.entity.Transfer;
import faang.school.paymentservice.event.transfer.TransferEventRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TransferRequestMapper {

    TransferRequest eventToDto(TransferEventRequest event);

    TransferEventRequest dtoToEvent(TransferRequest dto);

    @Mapping(target = "transferStatus", constant = "ACTIVE")
    @Mapping(target = "transferStage", constant = "AUTHORIZATION_PENDING")
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "updatedAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "sourceAccountId", source = "sourceId")
    @Mapping(target = "targetAccountId", source = "targetId")
    @Mapping(target = "transactionType", source = "category")
    Transfer dtoToEntity(TransferRequest dto);
}
