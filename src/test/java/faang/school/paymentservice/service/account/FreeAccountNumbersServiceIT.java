package faang.school.paymentservice.service.account;

import faang.school.paymentservice.config.TestContainersConfig;
import faang.school.paymentservice.entity.account.FreeAccountNumber;
import faang.school.paymentservice.model.account.AccountType;
import faang.school.paymentservice.repository.account.AccountNumbersSequenceRepository;
import faang.school.paymentservice.repository.account.FreeAccountNumbersRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;

@SpringBootTest
public class FreeAccountNumbersServiceIT extends TestContainersConfig {

    @Autowired
    private FreeAccountNumbersService accountNumbersService;

    @Autowired
    private FreeAccountNumbersRepository accountNumbersRepository;

    @Autowired
    private AccountNumbersSequenceRepository sequenceRepository;

    @Test
    void shouldReturnAndRemoveFreeAccountNumber() {
        String expectedNumber = "4276000000000011";
        accountNumbersRepository.save(new FreeAccountNumber(expectedNumber, AccountType.DEBIT));

        String usedNumber = accountNumbersService.useAccountNumber(AccountType.DEBIT, Function.identity());

        assertThat(usedNumber).isEqualTo(expectedNumber);
        assertThat(accountNumbersRepository.findById(expectedNumber)).isEmpty();
    }

    @Test
    void shouldFailWhenNoSequenceExists() {
        assertThrows(IllegalArgumentException.class, () ->
                accountNumbersService.useAccountNumber(AccountType.DEBIT, Function.identity())
        );
    }
}
