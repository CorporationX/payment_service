package faang.school.paymentservice.service;

import faang.school.paymentservice.dto.PaymentAccountDto;

public interface AccountService {
    PaymentAccountDto getPaymentAccount(Long id);
    PaymentAccountDto createPaymentAccount(PaymentAccountDto paymentAccountDto);
    PaymentAccountDto updatePaymentAccount(PaymentAccountDto paymentAccountDto);
    void deletePaymentAccount(Long id);
}
