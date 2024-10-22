package faang.school.paymentservice.mapper;

import faang.school.paymentservice.dto.PaymentAccountDto;
import faang.school.paymentservice.entity.PaymentAccount;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PaymentAccountMapper {
    PaymentAccountDto toDto(PaymentAccount paymentAccount);

    PaymentAccount toEntity(PaymentAccountDto paymentAccountDto);
}
