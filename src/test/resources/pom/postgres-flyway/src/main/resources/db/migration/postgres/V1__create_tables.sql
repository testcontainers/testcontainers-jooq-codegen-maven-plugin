create sequence user_id_seq start with 1 increment by 5;

create table users
(
    id                 bigint           DEFAULT nextval('user_id_seq') not null,
    email              varchar not null,
    password           varchar not null,
    name               varchar not null,
    role               varchar not null,
    verified           bool    not null default false,
    verification_token varchar,
    created_at         timestamp,
    updated_at         timestamp,
    primary key (id),
    CONSTRAINT user_email_unique UNIQUE (email)
);
