create
or replace function set_idempotency_key()
returns trigger as $$
begin
new.idempotency_key
= uuid_generate_v4();
return new;
end;
$$
language plpgsql

create trigger set_idempotency_key_trigger
    before insert on payments
    for each row
    execute function set_idempotency_key();