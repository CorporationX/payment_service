CREATE TABLE payment
(
    id                 BIGSERIAL PRIMARY KEY,
    idempotency_token  UUID           NOT NULL UNIQUE,
    from_account_id    BIGINT         NOT NULL,
    to_account_id      BIGINT         NOT NULL,
    amount             NUMERIC(19, 2) NOT NULL CHECK (amount > 0),
    currency           VARCHAR(10)    NOT NULL,
    status             VARCHAR(20)    NOT NULL,
    clear_scheduled_at TIMESTAMP,
    created_at         TIMESTAMP      NOT NULL DEFAULT now(),
    updated_at         TIMESTAMP      NOT NULL DEFAULT now()
);