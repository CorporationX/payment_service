CREATE TABLE payment_operations (
id UUID PRIMARY KEY,
sender_account_id UUID NOT NULL,
recipient_account_id UUID NOT NULL,
amount DECIMAL(19,4)
currency_code VARCHAR(3) NOT NULL,
payment_status VARCHAR(20) NOT NULL
CHECK(payment_status IN ('PENDING', 'AUTHORIZED', 'CLEARED', 'CANCELLED', 'FAILED')),
authorization_id UUID,
clear_scheduled_at TIMESTAMP WITH TIME ZONE,
created_at TIMESTAMP WITH TIME ZONE,
updated_at TIMESTAMP WITH TIME ZONE
version INTEGER NOT NULL DEFAULT 0
);