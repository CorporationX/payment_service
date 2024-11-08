package faang.school.paymentservice.mapper;

import faang.school.paymentservice.dto.PendingDto;
import faang.school.paymentservice.entity.Pending;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PendingMapper {

    List<PendingDto> toDto(List<Pending> pendings);
}
