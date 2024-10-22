package faang.school.paymentservice.dto;

import faang.school.paymentservice.entity.OwnerType;
import faang.school.paymentservice.entity.PaymentAccountCurrency;
import faang.school.paymentservice.entity.PaymentAccountStatus;
import faang.school.paymentservice.entity.PaymentAccountType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class PaymentAccountDto {
    private String number;
    private Long ownerId;
    private OwnerType ownerType;
    private PaymentAccountType type;
    private PaymentAccountCurrency currency;
    private PaymentAccountStatus status;
    private LocalDateTime createDate;
    private LocalDateTime changeDate;
    private LocalDateTime closeDate;
}
