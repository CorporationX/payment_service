package faang.school.paymentservice.service.payment;

import faang.school.paymentservice.dto.InvoiceDto;
import faang.school.paymentservice.model.Payment;
import org.springframework.stereotype.Component;

@Component
class PaymentValidator {
    public void checkPaymentFields(Payment payment, InvoiceDto invoiceDto) {
        if (!payment.getRequesterNumber().equals(invoiceDto.getRequesterNumber())) {
            throw new IllegalArgumentException("Check requester number");
        }
        if (!payment.getReceiverNumber().equals(invoiceDto.getReceiverNumber())) {
            throw new IllegalArgumentException("Check receiver number");
        }
        if (!payment.getCurrency().equals(invoiceDto.getCurrency())) {
            throw new IllegalArgumentException("Check currency");
        }
        if (!payment.getAmount().equals(invoiceDto.getAmount())) {
            throw new IllegalArgumentException("Check amount");
        }
        if (payment.getAmount().doubleValue() <= 0 || invoiceDto.getAmount().doubleValue() <= 0) {
            throw new IllegalArgumentException("Amount must be more than 0");
        }
    }
}