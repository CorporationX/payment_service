CREATE TABLE request
(
    id                 bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    sender_number      VARCHAR(255)   NOT NULL,
    recipient_number   VARCHAR(255)   NOT NULL,
    currency           VARCHAR(255)   NOT NULL,
    amount             DECIMAL(19, 4) NOT NULL,
    clear_scheduled_at TIMESTAMP       NOT NULL,
    status             VARCHAR(255)   NOT NULL,
    verification_code  VARCHAR(255)   NOT NULL,
    created_at         TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX index_status ON request (status);

