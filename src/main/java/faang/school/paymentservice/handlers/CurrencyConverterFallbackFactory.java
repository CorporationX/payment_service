package faang.school.paymentservice.handlers;

import faang.school.paymentservice.client.converter.CurrencyConverterClient;
import faang.school.paymentservice.dto.ExchangeRateDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

@Component
public class CurrencyConverterFallbackFactory implements FallbackFactory<CurrencyConverterClient> {

    private static final Logger logger = LoggerFactory.getLogger(CurrencyConverterFallbackFactory.class);

    @Override
    public CurrencyConverterClient create(Throwable cause) {
        logger.error("CurrencyConverterClient fallback triggered due to: {}", cause.getMessage());
        return new CurrencyConverterClient() {
            @Override
            public ExchangeRateDto getExchangeRates() {
                logger.warn("Returning empty ExchangeRateDto due to fallback.");
                return new ExchangeRateDto();
            }
        };
    }
}
