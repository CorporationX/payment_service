package faang.school.paymentservice.service.currency;

import faang.school.paymentservice.Mapper.CourseMapper;
import faang.school.paymentservice.dto.CourseDto;
import faang.school.paymentservice.dto.RatesDto;
import faang.school.paymentservice.service.redis.RedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CurrencyService {

    private final RetryableCurrencyFetcher retryableCurrencyFetcher;
    private final CourseMapper courseMapper;
    private final RedisService redisService;

    private List<CourseDto> courses;

    public void updateRates() {
        log.info("Started updating exchange rates");
        RatesDto ratesDto = retryableCurrencyFetcher.fetchDataWithRetry();
        courses = courseMapper.convert(ratesDto);
        if (courses != null) {
            for (CourseDto courseDto : courses) {
                String key = courseDto.getCurrencySent() + "/" + courseDto.getCurrencyReceived();
                redisService.save(key, courseDto.getRate());
            }
            log.info("Exchange rates updated");
        }
    }

    public Double convert(String currency, Long sum) {
        String key = "EUR/" + currency;
        Double rate = redisService.get(key);
        if (rate == null) {
            throw new IllegalArgumentException("Currency rate for " + key + "not found");
        }
        return rate * sum;
    }
}