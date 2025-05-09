CREATE TABLE IF NOT EXISTS payment_operation(
    id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    idempotency_token UUID NOT NULL UNIQUE,
    sender_id bigint NOT NULL,
    is_open boolean NOT NULL,
    receiver_id bigint NOT NULL,
    receiver_type varchar(64) NOT NULL,
    amount decimal(15, 2) NOT NULL CHECK (amount >= 1),
    currency varchar(16) NOT NULL,
    clear_scheduled_at timestamptz NOT NULL,
    status varchar(64) NOT NULL,
    version bigint NOT NULL,
    created_at timestamptz DEFAULT current_timestamp
);

CREATE INDEX IF NOT EXISTS idx_idempotency_token ON payment_operation (idempotency_token);
CREATE INDEX IF NOT EXISTS idx_clear_scheduled_at ON payment_operation (clear_scheduled_at);
CREATE INDEX IF NOT EXISTS idx_status ON payment_operation (status) WHERE status = 'PENDING';
CREATE UNIQUE INDEX IF NOT EXISTS uq_idx_sender_open ON payment_operation (sender_id) WHERE is_open = true;
