CREATE TABLE service_plans
(
    id           bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    name         varchar(64) NOT NULL UNIQUE,
    service_type varchar(64) NOT NULL,
    cost         int         NOT NULL,
    created_at   timestamptz DEFAULT current_timestamp,
    updated_at   timestamptz DEFAULT current_timestamp
);

CREATE TABLE orders
(
    id              bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    payment_method  varchar(32) NOT NULL,
    payment_status  varchar(16) NOT NULL,
    user_id         bigint      NOT NULL,
    service_plan_id bigint      NOT NULL,
    cost            int         NOT NULL,
    created_at      timestamptz DEFAULT current_timestamp,
    updated_at      timestamptz DEFAULT current_timestamp,

    CONSTRAINT fk_service_plan_id FOREIGN KEY (service_plan_id) REFERENCES service_plans (id)
);