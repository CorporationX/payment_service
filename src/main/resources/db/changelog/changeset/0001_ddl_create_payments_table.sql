create table payments (
    id serial primary key,
    requester_number varchar(32) not null,
    receiver_number varchar(32) not null,
    currency varchar(16) not null,
    amount numeric(30, 2) not null,
    status varchar(32) not null,
    idempotency_key uuid,
    created_at timestamp default current_timestamp,
    scheduled_at timestamp
)