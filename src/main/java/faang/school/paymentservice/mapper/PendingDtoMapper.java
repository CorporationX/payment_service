package faang.school.paymentservice.mapper;


import faang.school.paymentservice.dto.PendingDto;
import faang.school.paymentservice.entity.Pending;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PendingDtoMapper {

    Pending toEntity(PendingDto pendingDto);

    PendingDto toDto(Pending pending);
}
