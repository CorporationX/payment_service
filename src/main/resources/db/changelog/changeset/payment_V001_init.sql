CREATE TABLE payments(
    id UUID PRIMARY KEY DEFAULT gen_random_uuid() NOt NULL,
    amount NUMERIC(12, 2) NOT NULL,
    currency VARCHAR(8) NOT NULL,
    account_number_from VARCHAR(20) NOT NULL,
    account_number_to VARCHAR(20) NOT NULL,
    status VARCHAR(16) NOT NULL,
    clear_scheduled_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP DEFAULT current_timestamp NOT NULL
)