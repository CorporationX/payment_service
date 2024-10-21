package faang.school.paymentservice.mapper;


import faang.school.paymentservice.dto.account.AccountDto;
import faang.school.paymentservice.model.account.Account;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AccountMapper {

    @Mapping(target = "owner.externalId", source = "externalId")
    @Mapping(target = "owner.type", source = "ownerType")
    @Mapping(target = "type", source = "accountType")
    @Mapping(target = "status", source = "accountStatus")
    Account toAccountEntity(AccountDto accountDto);

    @Mapping(target = "externalId", source = "owner.externalId")
    @Mapping(target = "ownerType", source = "owner.type")
    @Mapping(target = "accountType", source = "type")
    @Mapping(target = "accountStatus", source = "status")
    AccountDto toAccountDto(Account account);
}
