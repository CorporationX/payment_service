package faang.school.paymentservice.service;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;

import faang.school.paymentservice.dto.Currency;

@Service
public interface CurrencyExchangeService {
    public BigDecimal exchange(BigDecimal amount, Currency currency);
}
