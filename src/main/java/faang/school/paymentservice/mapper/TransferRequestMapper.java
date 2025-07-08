package faang.school.paymentservice.mapper;

import faang.school.paymentservice.dto.transfer.TransferRequest;
import faang.school.paymentservice.entity.Transfer;
import faang.school.paymentservice.event.TransferEventRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TransferRequestMapper {

    TransferRequest eventToDto(TransferEventRequest event);

    TransferEventRequest dtoToEvent(TransferRequest dto);

    @Mapping(target = "active", defaultValue = "true")
    Transfer dtoToEntity(TransferRequest dto);
}
