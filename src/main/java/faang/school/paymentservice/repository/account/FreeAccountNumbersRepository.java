package faang.school.paymentservice.repository.account;

import faang.school.paymentservice.entity.account.FreeAccountNumber;
import faang.school.paymentservice.model.account.AccountType;

import java.util.Optional;

public interface FreeAccountNumbersRepository {
    void save(FreeAccountNumber account);
    Optional<FreeAccountNumber> findById(String accountNumber);
    Optional<String> fetchAndRemoveNextFreeNumber(AccountType accountType);
}

