package faang.school.paymentservice.service.account;

import faang.school.paymentservice.model.account.AccountType;
import faang.school.paymentservice.repository.account.AccountNumbersSequenceRepository;
import faang.school.paymentservice.repository.account.FreeAccountNumbersRepositoryImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.function.Function;

@Service
@RequiredArgsConstructor
@Slf4j
public class FreeAccountNumbersService {

    private static final int MAX_ATTEMPTS = 5;

    private final FreeAccountNumbersRepositoryImpl accountNumbersRepository;
    private final AccountNumbersSequenceRepository accountSequenceRepository;

    @Transactional
    public <T> T useAccountNumber(AccountType accountType, Function<String, T> consumer) {

        String accountNumber = accountNumbersRepository.fetchAndRemoveNextFreeNumber(accountType)
                .orElseThrow(() -> new IllegalArgumentException("Account not found for type: " + accountType));

        if (accountNumber == null) {
            accountNumber = generateNewNumber(accountType);
        }

        return consumer.apply(accountNumber);
    }

    private String generateNewNumber(AccountType accountType) {
        for (int attempt = 1; attempt <= MAX_ATTEMPTS; attempt++) {
            long current = accountSequenceRepository.getCurrentNumber(accountType)
                    .orElseThrow(() -> new IllegalStateException("Sequence not found for type: " + accountType));

            boolean updated = accountSequenceRepository.incrementIfEquals(accountType, current);

            if (updated) {
                return formatAccountNumber(accountType, current + 1);
            }

            log.warn("Optimistic lock failed for {} on attempt {}/{}", accountType, attempt, MAX_ATTEMPTS);
        }

        throw new IllegalStateException("Unable to generate account number for type: " + accountType +
                " after " + MAX_ATTEMPTS + " attempts");
    }

    private String formatAccountNumber(AccountType type, long number) {
        return type.getPrefix() + String.format("%012d", number);
    }
}
