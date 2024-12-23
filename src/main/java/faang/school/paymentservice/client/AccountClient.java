package faang.school.paymentservice.client;

import faang.school.paymentservice.dto.account.AccountDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "account-client", url = "${services.account-service.host}")
public interface AccountClient {
    @PostMapping("/number/{number}")
    AccountDto getAccount(@PathVariable String number);
}
