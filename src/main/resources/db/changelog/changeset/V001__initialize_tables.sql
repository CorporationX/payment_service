CREATE TABLE IF NOT EXISTS payment_operation (
    id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    idempotency_token UUID NOT NULL UNIQUE,
    sender_id bigint NOT NULL,
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
CREATE INDEX IF NOT EXISTS idx_status ON payment_operation (status);

CREATE TABLE IF NOT EXISTS request_outbox (
    id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    idempotency_token UUID NOT NULL UNIQUE,
    sender_id bigint NOT NULL,
    receiver_id bigint NOT NULL,
    receiver_type varchar(64) NOT NULL,
    amount decimal(15, 2) NOT NULL CHECK (amount >= 1),
    currency varchar(16) NOT NULL,
    event_type varchar(64) NOT NULL,
    sending_status varchar(64) NOT NULL,
    created_at timestamptz DEFAULT current_timestamp
);

CREATE INDEX IF NOT EXISTS idx_request_outbox_status_event_type ON request_outbox (event_type, sending_status);
