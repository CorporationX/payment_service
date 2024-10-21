package faang.school.paymentservice.service;

import faang.school.paymentservice.client.ProjectServiceClient;
import faang.school.paymentservice.client.UserServiceClient;
import faang.school.paymentservice.model.account.Account;
import faang.school.paymentservice.model.account.AccountStatus;
import faang.school.paymentservice.model.owner.Owner;
import faang.school.paymentservice.model.owner.OwnerType;
import faang.school.paymentservice.repository.AccountRepository;
import faang.school.paymentservice.repository.OwnerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;
    private final OwnerRepository ownerRepository;
    private final UserServiceClient userServiceClient;
    private final ProjectServiceClient projectServiceClient;

    @Transactional
    public Account create(Account account) {
        Owner owner = account.getOwner();
        Long externalId = owner.getExternalId();
        OwnerType ownerType = owner.getType();

        Owner exsistOwner = ownerRepository.findOwner(externalId, ownerType)
                .orElse(createOwner(externalId, ownerType));

        String newAccountNumber = generateAccountNumber();
        account.setAccountNumber(newAccountNumber);
        account.setStatus(AccountStatus.ACTIVE);
        account.setOwner(exsistOwner);

        return accountRepository.save(account);
    }

    private String generateAccountNumber() {
        //TODO: реализовать логику генерации номера счета
        return "00000000000000000000";
    }

    private Owner createOwner(Long externalId, OwnerType type) {
        Object ownerDto = switch (type) {
            case USER -> userServiceClient.getUser(externalId);
            case PROJECT -> projectServiceClient.getProject(externalId);
        };

        if (Objects.isNull(ownerDto)) {
            log.error("The {}={} does not exist", type, externalId);
            throw new IllegalArgumentException("Invalid externalId");
        }

        return ownerRepository.save(Owner.builder()
                .externalId(externalId)
                .type(type)
                .build());
    }
}
