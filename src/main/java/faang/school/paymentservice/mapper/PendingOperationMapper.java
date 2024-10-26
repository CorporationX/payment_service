package faang.school.paymentservice.mapper;

import faang.school.paymentservice.dto.PendingOperationDto;
import faang.school.paymentservice.model.OperationStatus;
import faang.school.paymentservice.model.PendingOperation;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import java.time.LocalDateTime;
import java.util.UUID;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PendingOperationMapper {
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "id", ignore = true)
    PendingOperation toEntity(PendingOperationDto dto);

    @AfterMapping
    default void setDefaults(@MappingTarget PendingOperation operation) {
        if (operation.getId() == null) {
            operation.setId(UUID.randomUUID());
        }
        if (operation.getStatus() == null) {
            operation.setStatus(OperationStatus.PENDING);
        }
        if (operation.getCreatedAt() == null) {
            operation.setCreatedAt(LocalDateTime.now());
        }
        operation.setUpdatedAt(LocalDateTime.now());
    }
}
