CREATE TABLE payments(
    id                  UUID PRIMARY KEY,
    amount              NUMERIC(12, 2)                          NOT NULL,
    currency            VARCHAR(8)                              NOT NULL,
    account_id_from     UUID                                    NOT NULL,
    account_id_to       UUID                                    NOT NULL,
    status              VARCHAR(32)                             NOT NULL,
    idempotency_key     VARCHAR(64)                             NOT NULL,
    clear_scheduled_at  TIMESTAMP                               NOT NULL,
    created_at          TIMESTAMP DEFAULT current_timestamp     NOT NULL,
    updated_at          TIMESTAMP DEFAULT current_timestamp     NOT NULL
)