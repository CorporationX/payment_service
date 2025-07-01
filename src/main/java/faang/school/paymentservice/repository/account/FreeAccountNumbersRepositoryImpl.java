package faang.school.paymentservice.repository.account;

import faang.school.paymentservice.entity.account.FreeAccountNumber;
import faang.school.paymentservice.model.account.AccountType;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class FreeAccountNumbersRepositoryImpl implements FreeAccountNumbersRepository {

    private final NamedParameterJdbcTemplate jdbc;

    @Override
    public void save(FreeAccountNumber account) {
        jdbc.update("""
            INSERT INTO free_account_numbers (account_number, account_type)
            VALUES (:number, :type)
        """, Map.of(
                "number", account.getAccountNumber(),
                "type", account.getAccountType().name()
        ));
    }

    @Override
    public Optional<FreeAccountNumber> findById(String accountNumber) {
        List<FreeAccountNumber> result = jdbc.query("""
            SELECT account_number, account_type
            FROM free_account_numbers
            WHERE account_number = :number
        """, Map.of("number", accountNumber), (rs, rowNum) -> new FreeAccountNumber(
                rs.getString("account_number"),
                AccountType.valueOf(rs.getString("account_type"))
        ));

        return result.stream().findFirst();
    }

    @Override
    public Optional<String> fetchAndRemoveNextFreeNumber(AccountType accountType) {
        return jdbc.query("""
            DELETE FROM free_account_numbers
            WHERE account_type = :type
            RETURNING account_number
        """, Map.of("type", accountType.name()), resultSet -> {
            return resultSet.next() ? Optional.of(resultSet.getString("account_number")) : Optional.empty();
        });
    }
}


