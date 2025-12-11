CREATE TABLE transfer (
    id                      uuid            PRIMARY KEY,
    sender_account_id       uuid            NOT NULL,
    recipient_account_id    uuid            NOT NULL,
    amount                  numeric(19, 2)  NOT NULL,
    product_category        varchar(255),
    clear_scheduled_at      timestamp       NOT NULL,
    status                  varchar(255)    NOT NULL,
    status_description      text,
    created_at              timestamp       NOT NULL DEFAULT now(),
    updated_at              timestamp
);

CREATE INDEX idx_transfer_clear_scheduled_at
    ON transfer (clear_scheduled_at)
    WHERE status IN ('AUTHORIZATION_SUCCESS');

CREATE TABLE transaction (
    id                   uuid           PRIMARY KEY,
    transfer_id          uuid,
    type_operation       varchar(255)   NOT NULL,
    status               varchar(255)   NOT NULL,
    status_description   text,
    created_at           timestamp      NOT NULL DEFAULT now(),
    updated_at           timestamp
);

ALTER TABLE transaction
    ADD CONSTRAINT fk_transaction_transfer
        FOREIGN KEY (transfer_id)
        REFERENCES transfer (id);
