create table status(
    name varchar(10) primary key
);

CREATE TABLE currency (
    code varchar(3) primary key NOT NULL
);
INSERT INTO currency(code)
VALUES ('EUR'), ('USD'), ('RUB');

INSERT INTO status (name)
VALUES ('SUCCESS'), ('CREATED'), ('REJECT');

create table payment(
    id bigserial primary key,
    status varchar(10) not null,
    payment_number bigint,
    request_id varchar(100) not null,
    amount numeric(10, 2) not null,
    currency varchar(3) not null,
    create_datetime timestamptz NOT NULL DEFAULT current_timestamp,
    expired_datetime timestamptz,
    paid_datetime timestamptz,
    product varchar(10) not null,

    foreign key (status) references status(name),
    foreign key (currency) references currency(code)
)