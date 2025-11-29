CREATE TABLE bank_operation (
    id                      uuid            PRIMARY KEY,
    sender_account_id       uuid            NOT NULL,
    recipient_account_id    uuid            NOT NULL,
    amount                  numeric(19, 2)  NOT NULL,
    type_operation          varchar(255)    NOT NULL,
    product_category        varchar(255),
    clear_scheduled_at      timestamp       NOT NULL,
    status                  varchar(255)    NOT NULL,
    status_description      text,
    created_at              timestamp       NOT NULL DEFAULT now(),
    updated_at              timestamp
);

CREATE INDEX idx_bank_operation_clear_scheduled_at
    ON bank_operation (clear_scheduled_at)
    WHERE status IN ('AUTHORIZATION_SUCCESS');

CREATE INDEX idx_bank_operation_status_error
    ON bank_operation (status)
    WHERE status IN ('AUTHORIZATION_ERROR', 'CLEARING_ERROR', 'CANCEL_ERROR');

CREATE TABLE transaction (
    id                   uuid           PRIMARY KEY,
    project_id           uuid,
    request_account_id   uuid           NOT NULL,
    type_operation       varchar(255)   NOT NULL,
    status               varchar(255)   NOT NULL,
    status_description   text,
    created_at           timestamp      NOT NULL DEFAULT now(),
    updated_at           timestamp
);

ALTER TABLE transaction
    ADD CONSTRAINT fk_transaction_bank_operation
        FOREIGN KEY (project_id)
        REFERENCES bank_operation (id);
