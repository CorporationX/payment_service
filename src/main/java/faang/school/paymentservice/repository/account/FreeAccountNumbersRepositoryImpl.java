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
        String sql = """
            INSERT INTO free_account_numbers (account_number, account_type)
            VALUES (:number, :type)
        """;

        jdbc.update(sql,
            Map.of(
            "number", account.getAccountNumber(),
            "type", account.getAccountType().name()
        ));
    }

    @Override
    public Optional<FreeAccountNumber> findById(String accountNumber) {
        String sql = """
            SELECT account_number, account_type
            FROM free_account_numbers
            WHERE account_number = :number
        """;

        List<FreeAccountNumber> result = jdbc.query(sql,
                Map.of("number", accountNumber), (resultSet, rowNum) -> new FreeAccountNumber(
                resultSet.getString("account_number"),
                AccountType.valueOf(resultSet.getString("account_type"))
        ));

        return result.stream().findFirst();
    }

    @Override
    public Optional<String> fetchAndRemoveNextFreeNumber(AccountType accountType) {
        String sql = """
            DELETE FROM free_account_numbers
            WHERE account_type = :type
            RETURNING account_number
        """;

        return jdbc.query(sql, Map.of("type", accountType.name()), resultSet -> {
            if (resultSet.next()) {
                return Optional.of(resultSet.getString("account_number"));
            }

            return Optional.empty();
        });
    }
}


