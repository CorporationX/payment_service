CREATE TABLE pending_operations (
    id         bigserial PRIMARY KEY,
    account_id bigserial NOT NULL,
    amount     numeric(10, 2),
    created_at timestamp,
    state      varchar(20) NOT NULL,
    operation_key varchar(128) NOT NULL
);

CREATE INDEX pending_state_index ON pending_operations(state);
CREATE INDEX pending_operation_key_index ON pending_operations(operation_key);