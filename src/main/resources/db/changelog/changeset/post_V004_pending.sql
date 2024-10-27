CREATE TABLE pending_operations (
    id         bigserial PRIMARY KEY,
    account_id bigserial NOT NULL,
    amount     numeric(10, 2),
    created_at timestamp,
    state      varchar(20),
    operation_key varchar(128)
);