package faang.school.paymentservice.service;

import faang.school.paymentservice.dto.exchange.CurrencyExchangeResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Component
@AllArgsConstructor
public class CurrencyApi {
//    private final RestTemplate restTemplate;
//
//    public CurrencyConverterClient(RestTemplate restTemplate) {
//        this.restTemplate = restTemplate;
//    }

//    public CurrencyExchangeResponse getCurrentCurrencyExchangeRate(String appId) {
//        String url = "https://api.exchangerate-api.com/v4/latest.json";
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
//
//        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url)
//                .queryParam("app_id", appId);
//
//        ResponseEntity<CurrencyExchangeResponse> responseEntity = restTemplate.getForEntity(builder.toUriString(), CurrencyExchangeResponse.class);
//        return responseEntity.getBody();
//    }
}