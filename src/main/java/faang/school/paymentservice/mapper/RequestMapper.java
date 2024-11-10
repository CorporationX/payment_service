package faang.school.paymentservice.mapper;

import faang.school.paymentservice.dto.request.RequestDto;
import faang.school.paymentservice.entity.request.Request;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RequestMapper {

    Request toRequest(RequestDto requestDto);

    RequestDto toRequestDto(Request request);
}
