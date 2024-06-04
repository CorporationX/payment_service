package faang.school.paymentservice.service.converter;

import faang.school.paymentservice.dto.convert.ConvertDto;

import java.math.BigDecimal;

public interface CurrencyConverterService {

    BigDecimal convert(ConvertDto convertDto);
}
