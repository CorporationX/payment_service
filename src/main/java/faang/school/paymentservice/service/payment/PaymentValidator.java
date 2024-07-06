package faang.school.paymentservice.service.payment;

import faang.school.paymentservice.dto.InvoiceDto;
import org.springframework.stereotype.Component;

@Component
class PaymentValidator {
    public void checkInvoiceFields(InvoiceDto invoiceDto) {
        if (invoiceDto.getAmount().doubleValue() <= 0) {
            throw new IllegalArgumentException("Amount must be more than 0");
        }
    }
}