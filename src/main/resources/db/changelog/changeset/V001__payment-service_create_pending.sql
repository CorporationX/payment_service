CREATE TABLE pending (
    id BIGSERIAL PRIMARY KEY,
    receiver_account_number VARCHAR(255) NOT NULL,
    owner_account_number VARCHAR(255) NOT NULL,
    idempotency_token VARCHAR(255) NOT NULL,
    amount VARCHAR(255) NOT NULL,
    currency VARCHAR(255) NOT NULL,
    status VARCHAR(255) NOT NULL DEFAULT 'CREATE',
    created_at timestamptz DEFAULT current_timestamp,
    updated_at timestamptz DEFAULT current_timestamp,
    closed_at timestamptz ,
    version bigint DEFAULT 1
);

CREATE INDEX idx_idempotency_token ON pending (idempotency_token);