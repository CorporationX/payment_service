package faang.school.paymentservice.dto;

import faang.school.paymentservice.model.Currency;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentRequestDto {
   private long paymentNumber;
   private BigDecimal amount;
   private Currency currency;
}
