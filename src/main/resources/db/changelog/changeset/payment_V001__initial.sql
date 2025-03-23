CREATE TABLE payment
(
    id                      UUID PRIMARY KEY,
    sender_account_number   VARCHAR(20)    NOT NULL,
    receiver_account_number VARCHAR(20)    NOT NULL,
    amount                  NUMERIC(19, 2) NOT NULL,
    currency                CHAR(3)        NOT NULL,
    creat_at                TIMESTAMP      NOT NULL DEFAULT current_timestamp,
    payment_date_time       TIMESTAMP      NOT NULL DEFAULT current_timestamp,
    payment_type            VARCHAR(64)    NOT NULL DEFAULT 'OTHER',
    payment_status          VARCHAR(64)    NOT NULL DEFAULT 'NEW',
    version                 INT            NOT NULL DEFAULT 0,

    CHECK (CHAR_LENGTH(sender_account_number) BETWEEN 12 AND 20),
    CHECK (CHAR_LENGTH(payment.receiver_account_number) BETWEEN 12 AND 20)
);

CREATE INDEX idx_payment_sender_number ON payment (sender_account_number);
CREATE INDEX idx_payment_receiver_number ON payment (receiver_account_number);