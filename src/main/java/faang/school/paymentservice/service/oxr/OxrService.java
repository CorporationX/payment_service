package faang.school.paymentservice.service.oxr;

import faang.school.paymentservice.dto.ExchangeResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class OxrService {
    @Value("${oxr.app-id}")
    private String appId;
    @Value("${oxr.base-url}")
    private String baseUrl;
    private final RestTemplate restTemplate = new RestTemplate();

    @Retryable(value = {NoSuchElementException.class}, maxAttempts = 5,
            backoff = @Backoff(value = 2000, multiplier = 2))
    public ExchangeResponseDto getLatestRates() {
        URI uri = UriComponentsBuilder.fromHttpUrl(baseUrl)
                .path("/latest.json")
                .queryParam("app_id", appId)
                .build()
                .toUri();

        return restTemplate.getForObject(uri, ExchangeResponseDto.class);
    }
}
