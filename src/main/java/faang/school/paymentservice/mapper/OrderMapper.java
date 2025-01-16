package faang.school.paymentservice.mapper;

import faang.school.paymentservice.dto.order.OrderDto;
import faang.school.paymentservice.entity.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface OrderMapper {

    @Mapping(source = "servicePlan.name", target = "servicePlan")
    @Mapping(source = "servicePlan.serviceType.name", target = "serviceType")
    OrderDto toDto(Order order);
}
