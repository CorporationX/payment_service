CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE transfer (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    initiator_id bigint NOT NULL,
    account_event_id UUID DEFAULT uuid_generate_v4(),
    source_account_id UUID DEFAULT uuid_generate_v4(),
    target_account_id UUID DEFAULT uuid_generate_v4(),
    transfer_status varchar(16),
    transaction_status varchar(16),
    transaction_type varchar(16),
    created_at TIMESTAMP NOT NULL DEFAULT current_timestamp,
    updated_at TIMESTAMP NOT NULL,
    cleared_at TIMESTAMP NOT NULL,
    version INTEGER NOT NULL DEFAULT 1,
);