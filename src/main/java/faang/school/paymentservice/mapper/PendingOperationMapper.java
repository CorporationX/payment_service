package faang.school.paymentservice.mapper;

import faang.school.paymentservice.model.dto.PendingOperationDto;
import faang.school.paymentservice.model.entity.PendingOperation;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedSourcePolicy = ReportingPolicy.IGNORE, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PendingOperationMapper {

    PendingOperation toEntity(PendingOperationDto pendingOperationDto);

    PendingOperationDto toDto(PendingOperation pendingOperation);
}
