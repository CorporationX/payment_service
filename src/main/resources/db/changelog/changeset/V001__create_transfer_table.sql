CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE transfer (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    initiator_id bigint NOT NULL,
    account_event_id UUID DEFAULT uuid_generate_v4(),
    source_account_id UUID DEFAULT uuid_generate_v4(),
    target_account_id UUID DEFAULT uuid_generate_v4(),
    transfer_status varchar(32),
    transaction_status varchar(32),
    transaction_type varchar(32),
    created_at TIMESTAMP NOT NULL DEFAULT current_timestamp,
    updated_at TIMESTAMP NOT NULL,
    cleared_at TIMESTAMP,
    description varchar(128),
    version INTEGER NOT NULL DEFAULT 1
);