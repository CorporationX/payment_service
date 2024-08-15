package faang.school.paymentservice.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "currency-converter", url = "${currency-converter.foreignConverterUrl}")
public interface CurrencyConverterClient {
    @GetMapping("${currency-converter.foreignConverterPath}")
    ResponseEntity<String> getCurrencyRate(
            @RequestParam("app_id") String appId,
            @RequestParam("base") String base,
            @RequestParam("symbols") String symbols
    );
}
