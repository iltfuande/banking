create type transaction_type_enum as enum ('DEPOSIT', 'WITHDRAW', 'TRANSFER');

create cast (character varying as transaction_type_enum) with inout as assignment;