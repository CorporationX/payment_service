package faang.school.paymentservice.mapper;

import faang.school.paymentservice.dto.PaymentDto;
import faang.school.paymentservice.model.Payment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.math.BigDecimal;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PaymentMapper {

    @Mapping(target = "amount", source = "amount", qualifiedByName = "amountConverterToNum")
    Payment toPaymentEntity(PaymentDto paymentDto);

//    @Mapping(target = "amount", source = "amount", qualifiedByName = "amountConverterToString")
    PaymentDto toPaymentDto(Payment payment);

    @Named("amountConverterToNum")
    static BigDecimal amountConverter(String amount) {
        return new BigDecimal(amount);
    }

//    @Named("amountConverterToString")
//    static String amountConverter(BigDecimal amount) {
//        return amount.toString();
//    }
}
