CREATE TABLE payment_operations (
id UUID PRIMARY KEY,
sender_account_id UUID NOT NULL,
recipient_account_id UUID NOT NULL,
amount DECIMAL(19,4),

currency_code VARCHAR(3) NOT NULL
CHECK(currency_code IN ('USD', 'EUR')),

payment_status VARCHAR(20) NOT NULL
CHECK(payment_status IN ('PENDING', 'AUTHORIZED', 'CLEARED', 'CANCELED', 'FAILED')),

authorization_id UUID,
clear_scheduled_at TIMESTAMP WITH TIME ZONE,
created_at TIMESTAMP WITH TIME ZONE,
updated_at TIMESTAMP WITH TIME ZONE
);

CREATE TABLE outbox_event(
id UUID PRIMARY KEY,
payment_operation_id UUID NOT NULL,

event_type VARCHAR(50) NOT NULL
CHECK(event_type IN ('PENDING', 'AUTHORIZED', 'CLEARED', 'CANCELED', 'FAILED')),

payload JSONB NOT NULL,
outbox_status VARCHAR(20) NOT NULL DEFAULT 'NEW'
CHECK (outbox_status IN ('NEW', 'SENT', 'ERROR')),

created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
sent_at TIMESTAMP WITH TIME ZONE
);

CREATE INDEX idx_payment_operations_status_scheduled
ON payment_operations (payment_status, clear_scheduled_at);