CREATE TABLE pending_operation (
    id UUID PRIMARY KEY,
    account_id UUID NOT NULL,
    idempotency_key VARCHAR(255) NOT NULL UNIQUE,
    amount bigint NOT NULL,
    currency VARCHAR(3) NOT NULL,
    status VARCHAR(20) NOT NULL,
    clear_scheduled_at timestamptz NOT NULL,
    created_at timestamptz NOT NULL DEFAULT current_timestamp,
    updated_at timestamptz DEFAULT current_timestamp
);

CREATE INDEX index_pending_operation_status ON pending_operation (status);
CREATE INDEX index_pending_operation_status_clear_scheduled_at ON pending_operation (status, clear_scheduled_at);
