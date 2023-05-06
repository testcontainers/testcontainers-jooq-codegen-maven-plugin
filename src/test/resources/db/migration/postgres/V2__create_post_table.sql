create sequence post_id_seq start with 1 increment by 5;

create table posts
(
    id         bigint DEFAULT nextval('post_id_seq') not null,
    title      varchar                               not null,
    content    varchar                               not null,
    created_at timestamp,
    updated_at timestamp,
    primary key (id)
);
