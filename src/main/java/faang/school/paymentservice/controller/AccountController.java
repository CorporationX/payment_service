package faang.school.paymentservice.controller;

import faang.school.paymentservice.dto.account.AccountDto;
import faang.school.paymentservice.mapper.AccountMapper;
import faang.school.paymentservice.model.account.Account;
import faang.school.paymentservice.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
public class AccountController {
    private final AccountService accountService;
    private final AccountMapper accountMapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AccountDto create(@RequestBody @Valid AccountDto accountDto) {
        Account account = accountService.create(accountMapper.toAccountEntity(accountDto));
        return accountMapper.toAccountDto(account);
    }


}
