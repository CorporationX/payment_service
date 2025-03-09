INSERT INTO payment (id, sender_account_number, receiver_account_number, amount, currency, creat_at, payment_date_time,
                     payment_type, payment_status, version)
VALUES ('550e8400-e29b-41d4-a716-446655440000', '123456789012',
        '987654321098', 100.50, 'USD', CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP, 'OTHER', 'PROCESS_OF_CANCELLATION', 1);