package faang.school.paymentservice.mapper;

import faang.school.paymentservice.dto.event.dmsevent.DmsEventDto;
import faang.school.paymentservice.entity.request.Request;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EventMapper {

    @Mapping(source = "id", target = "requestId")
    DmsEventDto requestToDmsEventDto(Request request);
}
