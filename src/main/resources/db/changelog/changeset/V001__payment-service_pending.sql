CREATE SEQUENCE pending_id_seq START 1;
CREATE TABLE pending (
    id BIGINT PRIMARY KEY DEFAULT nextval('pending_id_seq'),
    senders_account_number VARCHAR(32) NOT NULL,
    recipient_id BIGINT NOT NULL,
    balance DECIMAL(15,2) NOT NULL DEFAULT 0.00,
    currency VARCHAR(16) NOT NULL,
    operation_id BIGINT NOT NULL,
    request_type VARCHAR(32) NOT NULL,
    status VARCHAR(32) NOT NULL,
    reason VARCHAR(256),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    completion_date TIMESTAMP DEFAULT (CURRENT_TIMESTAMP + INTERVAL '1 day')
);

CREATE INDEX idx_pending_account_number ON pending(senders_account_number);

