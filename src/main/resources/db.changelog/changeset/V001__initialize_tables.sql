CREATE TABLE IF NOT EXISTS payment_operation(
    id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    sender_id bigint NOT NULL,
    sender_type varchar(64) NOT NULL,
    receiver_id bigint NOT NULL,
    receiver_type varchar(64) NOT NULL,
    amount decimal(15, 2) NOT NULL CHECK (amount >= 1),
    currency varchar(16) NOT NULL,
    status varchar(64) NOT NULL,
    clear_scheduled_at timestamptz NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_clear_scheduled_at ON payment_operation (clear_scheduled_at);
CREATE INDEX IF NOT EXISTS idx_status ON payment_operation (status) WHERE status = 'PENDING';
