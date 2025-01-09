package faang.school.paymentservice.mapper;

import faang.school.paymentservice.dto.PaymentRequest;
import faang.school.paymentservice.entity.Pending;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PaymentMapper {
    Pending toEntity (PaymentRequest paymentRequest);

}
