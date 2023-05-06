create table users
(
    id                 bigint           not null AUTO_INCREMENT,
    email              varchar(255) not null,
    password           varchar(255) not null,
    name               varchar(255) not null,
    role               varchar(255) not null,
    verified           bool    not null default false,
    verification_token varchar(255),
    created_at         timestamp,
    updated_at         timestamp,
    PRIMARY KEY (id),
    CONSTRAINT user_email_unique UNIQUE (email)
);
