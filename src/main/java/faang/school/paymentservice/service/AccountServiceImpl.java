package faang.school.paymentservice.service;

import faang.school.paymentservice.dto.PaymentAccountDto;
import faang.school.paymentservice.entity.PaymentAccount;
import faang.school.paymentservice.mapper.PaymentAccountMapper;
import faang.school.paymentservice.repository.PaymentAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final PaymentAccountRepository paymentAccountRepository;
    private final PaymentAccountMapper paymentAccountMapper;

    @Transactional
    @Override
    public PaymentAccountDto getPaymentAccount(Long id) {
        PaymentAccount paymentAccount = paymentAccountRepository.findById(id).orElseThrow(IllegalArgumentException::new);
        return paymentAccountMapper.toDto(paymentAccount);
    }

    @Transactional
    @Override
    public PaymentAccountDto createPaymentAccount(PaymentAccountDto paymentAccountDto) {
        PaymentAccount paymentAccount = paymentAccountMapper.toEntity(paymentAccountDto);
        paymentAccountRepository.save(paymentAccount);
        return paymentAccountDto;
    }

    @Transactional
    @Override
    public PaymentAccountDto updatePaymentAccount(PaymentAccountDto paymentAccountDto) {
        PaymentAccount paymentAccount = paymentAccountMapper.toEntity(paymentAccountDto);
        paymentAccountRepository.save(paymentAccount);
        return paymentAccountDto;
    }

    @Transactional
    @Override
    public void deletePaymentAccount(Long id) {
        paymentAccountRepository.deleteById(id);
    }
}
