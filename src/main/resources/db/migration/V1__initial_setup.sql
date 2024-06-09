create table if not exists account
(
    id                             varchar(255) not null
    primary key,
    comment_alarm                  boolean      not null,
    created_time                   timestamp(6),
    favorite_author                varchar(255),
    favorite_quotation             varchar(255),
    identity_verification_answer   varchar(255),
    identity_verification_question varchar(255),
    last_modified_time             timestamp(6),
    nickname                       varchar(255),
    password                       varchar(255),
    profile_path                   varchar(255),
    quotation_alarm                boolean      not null,
    quotation_alarm_times          timestamp without time zone[]
);

create table if not exists author
(
    id                 uuid not null
    primary key,
    country_code       varchar(255),
    created_time       timestamp(6),
    last_modified_time timestamp(6),
    name               varchar(255)
);

create table if not exists bookmark
(
    id                 uuid    not null
    primary key,
    created_time       timestamp(6),
    icon               varchar(255),
    last_modified_time timestamp(6),
    name               varchar(255),
    quotation_ids      uuid[],
    user_id            varchar(255),
    visibility         boolean not null
);

create table if not exists comment
(
    id                 uuid not null
    primary key,
    content            varchar(255),
    created_time       timestamp(6),
    last_modified_time timestamp(6),
    parent_id          uuid,
    quotation_id       uuid,
    user_id            varchar(255),
    commented_user_id  varchar(255)
    );

create table if not exists notification
(
    id                 uuid    not null
    primary key,
    alarm_check        boolean not null,
    comment_id         uuid,
    commented_user_id  varchar(255),
    commenter_id       varchar(255),
    created_time       timestamp(6),
    last_modified_time timestamp(6)
);

create table if not exists prefer
(
    id                 uuid not null
    primary key,
    created_time       timestamp(6),
    last_modified_time timestamp(6),
    quotation_id       uuid,
    user_id            varchar(255)
);

create table if not exists quotation
(
    id                    uuid   not null
    primary key,
    author_id             uuid,
    background_image_path varchar(255),
    comment_count         bigint not null,
    content               text,
    created_time          timestamp(6),
    last_modified_time    timestamp(6),
    like_count            bigint not null,
    share_count           bigint not null
);
