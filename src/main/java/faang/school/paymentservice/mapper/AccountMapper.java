package faang.school.paymentservice.mapper;

import faang.school.paymentservice.dto.account.Account;
import faang.school.paymentservice.dto.account.AccountCreateDto;
import faang.school.paymentservice.dto.account.AccountDto;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AccountMapper {
    Account fromCreateDto(AccountCreateDto accountDto);

    AccountDto toDto(Account account);
}
