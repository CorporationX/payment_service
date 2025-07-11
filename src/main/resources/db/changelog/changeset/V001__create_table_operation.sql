--liquibase formatted sql

--changeset sanya_popenko:create_uuid_ossp_extension
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

--changeset sanya_popenko:create_payment_operation_table
--comment: Создание таблицы payment_operation с ограничением на сумму и audit-полями
CREATE TABLE payment_operation (
    id                 UUID PRIMARY KEY,
    operation_token    UUID NOT NULL UNIQUE,
    account_from_id    UUID NOT NULL,
    account_to_id      UUID NOT NULL,
    currency_id        UUID NOT NULL,
    amount             NUMERIC(20, 10) NOT NULL CHECK (amount >= 1 AND amount <= 10000000),
    status             VARCHAR(32) NOT NULL,
    created_at         TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at         TIMESTAMP NOT NULL,
    clear_scheduled_at TIMESTAMP NOT NULL
);