CREATE TABLE IF NOT EXISTS pending_operation (
    id                          BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    receiver_account_number     VARCHAR(20) NOT NULL,
    owner_account_number        VARCHAR(20) NOT NULL,
    currency                    VARCHAR(5) NOT NULL,
    amount                      NUMERIC NOT NULL,
    status                      VARCHAR(255) NOT NULL,
    idempotency_token           VARCHAR(255) NOT NULL,
    clear_scheduled_at          TIMESTAMP DEFAULT (CURRENT_TIMESTAMP + INTERVAL '15 minutes'),
    created_at                  timestamptz DEFAULT current_timestamp,
    updated_at                  timestamptz DEFAULT current_timestamp,

    CONSTRAINT fk_receiver_account_number FOREIGN KEY (receiver_account_number) REFERENCES account (number),
    CONSTRAINT fk_owner_account_number FOREIGN KEY (owner_account_number) REFERENCES account (number)
);

CREATE OR REPLACE FUNCTION update_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at := current_timestamp;
RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER update_account_updated_at
    BEFORE UPDATE ON pending_operation
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at();