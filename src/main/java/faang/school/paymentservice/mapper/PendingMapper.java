package faang.school.paymentservice.mapper;

import faang.school.paymentservice.dto.PendingDto;
import faang.school.paymentservice.entity.Pending;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PendingMapper {

    PendingDto toDto(Pending pending);
}
