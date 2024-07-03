CREATE TABLE IF NOT EXISTS payment_request
(
    id                 BIGINT PRIMARY KEY,
    debit_account_id   BIGINT      NOT NULL,
    credit_account_id  BIGINT      NOT NULL,
    amount             DECIMAL      NOT NULL,
    currency           VARCHAR(3)  NOT NULL,
    type               VARCHAR(16) NOT NULL,
    is_cleared         BOOLEAN     NOT NULL,
    clear_scheduled_at TIMESTAMPTZ NOT NULL,
    created_at         TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    updated_at         TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_debit_account_id FOREIGN KEY (debit_account_id) REFERENCES account (id),
    CONSTRAINT fk_credit_account_id FOREIGN KEY (credit_account_id) REFERENCES account (id)
);