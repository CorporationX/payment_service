package faang.school.paymentservice.mapper;

import faang.school.paymentservice.dto.pending.PendingDto;
import faang.school.paymentservice.dto.pending.PendingRequestDto;
import faang.school.paymentservice.dto.pending.ResponseClearingDto;
import faang.school.paymentservice.model.Pending;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PendingMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "requestStatus", ignore = true)
    @Mapping(target = "reason", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Pending toEntity(PendingDto dto);

    @Mapping(target = "token", ignore = true)
    PendingDto toDto(Pending pending);

    PendingRequestDto toRequest(PendingDto dto);

    ResponseClearingDto toClearing(Pending pending);
}
