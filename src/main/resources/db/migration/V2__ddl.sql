create table account
(
    id               serial primary key,
    account_number   uuid unique default gen_random_uuid(),
    owner_name       varchar(100)   not null,
    balance          decimal(15, 2) not null check (balance >= 0),
    create_date_time timestamp      not null,
    update_date_time timestamp
);

create table transaction
(
    id                  serial primary key,
    account_number_to   uuid references account (account_number),
    account_number_from uuid references account (account_number),
    amount              decimal(15, 2)        not null check (amount > 0),
    transaction_type    transaction_type_enum not null,
    create_date_time    timestamp             not null
);

alter table transaction
    add constraint at_least_one_account_present
        check (
                account_number_from is not null or account_number_to is not null
            );