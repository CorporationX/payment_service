CREATE TABLE IF NOT EXISTS payment_request
(
    id                     UUID PRIMARY KEY,
    idempotency_key        VARCHAR(64)  NOT NULL,
    source_account_id      UUID         NOT NULL,
    target_account_id      UUID         NOT NULL,
    amount                 BIGINT       NOT NULL,
    currency               VARCHAR(3)   NOT NULL,
    status                 VARCHAR(20)  NOT NULL,
    category               VARCHAR(50)  NOT NULL,
    clear_scheduled_at     TIMESTAMPTZ  NOT NULL,
    created_at             TIMESTAMPTZ  NOT NULL DEFAULT current_timestamp,
    updated_at             TIMESTAMPTZ           DEFAULT current_timestamp
);

CREATE INDEX idempotency_key_index ON payments(idempotency_key);