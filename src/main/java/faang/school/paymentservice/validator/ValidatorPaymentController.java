package faang.school.paymentservice.validator;

import faang.school.paymentservice.dto.Currency;
import org.apache.commons.lang3.EnumUtils;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.stereotype.Component;

@Component
public class ValidatorPaymentController {
    public void checkCurrency(Currency currency) {
        if (!EnumUtils.isValidEnum(Currency.class, currency.name())) {
            throw new HttpMessageNotReadableException(null);
        }
    }
}
