package faang.school.paymentservice.client.account_service;


import faang.school.paymentservice.dto.account.AccountDto;
import faang.school.paymentservice.dto.account.QueryType;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "account-service",  url = "${account-service.host}:${account-service.port}/accounts")
public interface AccountServiceClient {

    @GetMapping
    List<AccountDto> getAccountByNumber(@RequestParam QueryType query, @RequestParam String number);
}
