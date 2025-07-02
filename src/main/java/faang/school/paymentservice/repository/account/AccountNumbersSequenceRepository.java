package faang.school.paymentservice.repository.account;

import faang.school.paymentservice.model.account.AccountType;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class AccountNumbersSequenceRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public void save(AccountType accountType, long currentNumber) {
        String sql = """
            INSERT INTO account_numbers_sequence (account_type, current_number)
            VALUES (:type, :current)
            ON CONFLICT (account_type) DO UPDATE
            SET current_number = EXCLUDED.current_number
        """;

        jdbcTemplate.update(sql, Map.of(
                "type", accountType.name(),
                "current", currentNumber
        ));
    }

    public boolean incrementIfEquals(AccountType accountType, long expected) {
        String sql = """
            UPDATE account_numbers_sequence
            SET current_number = :next
            WHERE account_type = :type AND current_number = :expected
        """;

        int updated = jdbcTemplate.update(sql, Map.of(
                "next", expected + 1,
                "type", accountType.name(),
                "expected", expected
        ));

        return updated == 1;
    }

    public Optional<Long> getCurrentNumber(AccountType accountType) {
        String sql = """
            SELECT current_number
            FROM account_numbers_sequence
            WHERE account_type = :type
        """;

        return Optional.ofNullable(jdbcTemplate.queryForObject(
                sql,
                Map.of("type", accountType.name()),
                Long.class
        ));
    }
}
