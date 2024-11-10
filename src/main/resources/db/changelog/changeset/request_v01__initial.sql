drop table if exists public.request;
create table public.request (
   id bigint primary key generated always as identity,
   sender_id bigint not null,
   receiver_id bigint not null,
   amount numeric(16, 2) not null,
   currency varchar(64) not null,
   status varchar(64) not null,
   clear_scheduled_at timestamp not null,
   version bigint not null,
   created_at timestamp default now(),
   updated_at timestamp default now()
);