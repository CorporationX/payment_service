package faang.school.paymentservice.service;

import faang.school.paymentservice.dto.Currency;
import faang.school.paymentservice.dto.PaymentRequest;
import faang.school.paymentservice.dto.PaymentResponse;
import faang.school.paymentservice.dto.PaymentStatus;
import faang.school.paymentservice.dto.Rate;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class ConvertServiceImpl implements ConvertService {
    private final RedisCacheManager manager;
    private final CurrencyServiceImpl currencyService;

    @Value("${exchange_rates.base}")
    private String base;

    @Value("${spring.cache.redis.caches.current_rate}")
    private String rateCache;

    @Override
    public PaymentResponse getConvertedPayment(PaymentRequest dto) {
        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        String formattedSum = decimalFormat.
                format(convert(dto.currency(), Currency.valueOf(base), dto.amount()));
        int verificationCode = new Random().nextInt(1000, 10000);
        String message = String.format("Dear friend! Thank you for your purchase! " +
                        "Your payment on %s %s was accepted.",
                formattedSum, dto.currency().name());
        return new PaymentResponse(
                PaymentStatus.SUCCESS,
                verificationCode,
                dto.paymentNumber(),
                dto.amount(),
                dto.currency(),
                message);
    }

    private BigDecimal convert(Currency convertable, Currency base, BigDecimal sum) {
        Rate rate = addCommission(convertable, base);
        return sum.multiply(BigDecimal.valueOf(rate.getRates().get(convertable.getName())));
    }

    private Rate addCommission(Currency convertable, Currency base1) {
        Rate rate = (Rate) Objects.requireNonNull(manager.getCache(rateCache))
                .get("current_rate")
                .get();

        if (rate == null) {
            rate = currencyService.updateCurrency();
        }

        Map<String, Double> mapRate = rate.getRates();
        Map<String, Double> newMapRate = new HashMap<>();
        if (Objects.equals(convertable.getName(), rate.getBase())) {
            newMapRate.put(base1.getName(), mapRate.get(base1.getName()) * 0.99);
        } else {
            double baseCur = mapRate.get(convertable.getName());
            double convertableCur = mapRate.get(base1.getName());

            newMapRate.put(base1.getName(), convertableCur / baseCur);
        }
        rate.setRates(newMapRate);

        return rate;
    }
}
