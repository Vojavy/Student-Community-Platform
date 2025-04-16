create table public.roles
(
    id   integer generated always as identity
        constraint roles_pk
            primary key,
    name varchar not null
);

alter table public.roles
    owner to admin;

create table public.university_domains
(
    id          serial
        primary key,
    domain_name varchar(255) not null,
    website_url varchar(255) not null,
    admin_email varchar(255) not null
);

alter table public.university_domains
    owner to admin;

create table public.users
(
    id                   bigint generated always as identity
        constraint users_pk
            primary key,
    auth_provider        varchar(255) not null
        constraint table_name_auth_provider_check
            check ((auth_provider)::text = ANY
                   (ARRAY [('local'::character varying)::text, ('microsoft'::character varying)::text])),
    provider_id          integer      not null,
    email                varchar(255),
    password             varchar(512),
    active               boolean      not null,
    created_at           timestamp    not null,
    updated_at           timestamp    not null,
    domain_id            integer
        constraint fk_users_domains
            references public.university_domains
            on delete set null,
    verification_code    varchar(6)
        constraint chk_user_verification_code_length
            check ((verification_code)::text ~ '^[0-9]{6}$'::text),
    verification_expires timestamp
);

alter table public.users
    owner to admin;

create table public.user_roles
(
    user_id integer not null
        references public.users
            on delete cascade,
    role_id integer not null
        references public.roles
            on delete cascade,
    primary key (user_id, role_id)
);

alter table public.user_roles
    owner to admin;

create table public.user_tokens
(
    id         serial
        primary key,
    user_id    integer      not null
        references public.users
            on delete cascade,
    token      varchar(255) not null,
    token_type varchar(50)  not null
        constraint user_tokens_token_type_check
            check ((token_type)::text = ANY
                   ((ARRAY ['auth'::character varying, 'refresh'::character varying, 'verify'::character varying])::text[])),
    expiration timestamp    not null,
    created_at timestamp default now()
);

alter table public.user_tokens
    owner to admin;

create table public.groups
(
    id                  serial
        primary key,
    name                varchar(255) not null,
    description         text,
    topics              jsonb,
    created_at          timestamp   default now(),
    is_public           boolean     default true,
    min_role_for_posts  varchar(50) default 'member'::character varying
        constraint groups_min_role_for_posts_check
            check ((min_role_for_posts)::text = ANY
                   (ARRAY [('owner'::character varying)::text, ('admin'::character varying)::text, ('editor'::character varying)::text, ('member'::character varying)::text])),
    min_role_for_events varchar(50) default 'admin'::character varying
        constraint groups_min_role_for_events_check
            check ((min_role_for_events)::text = ANY
                   (ARRAY [('owner'::character varying)::text, ('admin'::character varying)::text, ('editor'::character varying)::text, ('member'::character varying)::text]))
);

alter table public.groups
    owner to admin;

create table public.group_posts
(
    id         serial
        primary key,
    group_id   integer not null
        references public.groups
            on delete cascade,
    user_id    integer not null
        references public.users
            on delete cascade,
    content    jsonb   not null,
    created_at timestamp default now(),
    updated_at timestamp
);

alter table public.group_posts
    owner to admin;

create table public.group_announcements
(
    id         serial
        primary key,
    group_id   integer      not null
        references public.groups
            on delete cascade,
    title      varchar(255) not null,
    content    text         not null,
    created_by integer      not null
        references public.users
            on delete cascade,
    created_at timestamp default now()
);

alter table public.group_announcements
    owner to admin;

create table public.group_attachments
(
    id          serial
        primary key,
    group_id    integer      not null
        references public.groups
            on delete cascade,
    uploaded_by integer      not null
        references public.users
            on delete cascade,
    file_url    varchar(255) not null,
    uploaded_at timestamp default now()
);

alter table public.group_attachments
    owner to admin;

create table public.user_group_memberships
(
    id        serial
        primary key,
    user_id   integer     not null
        references public.users
            on delete cascade,
    group_id  integer     not null
        references public.groups
            on delete cascade,
    status    varchar(50) not null
        constraint user_group_memberships_status_check
            check ((status)::text = ANY
                   (ARRAY [('approved'::character varying)::text, ('pending'::character varying)::text, ('banned'::character varying)::text])),
    role      varchar(50) not null
        constraint user_group_memberships_role_check
            check ((role)::text = ANY
                   (ARRAY [('owner'::character varying)::text, ('admin'::character varying)::text, ('editor'::character varying)::text, ('member'::character varying)::text])),
    joined_at timestamp default now(),
    constraint unique_user_in_group
        unique (user_id, group_id)
);

alter table public.user_group_memberships
    owner to admin;

create table public.sale_items
(
    id          serial
        primary key,
    title       varchar(255)                                    not null,
    description text,
    price       numeric(10, 2),
    topics      jsonb,
    created_at  timestamp   default now(),
    created_by  integer                                         not null
        references public.users
            on delete cascade,
    status      varchar(50) default 'active'::character varying not null
        constraint sale_items_status_check
            check ((status)::text = ANY
                   (ARRAY [('active'::character varying)::text, ('closed'::character varying)::text])),
    images      jsonb,
    is_local    boolean     default false
);

alter table public.sale_items
    owner to admin;

create table public.forum_posts
(
    id         serial
        primary key,
    forum_id   integer not null,
    author_id  integer not null
        references public.users
            on delete cascade,
    content    jsonb   not null,
    created_at timestamp default now(),
    updated_at timestamp
);

alter table public.forum_posts
    owner to admin;

create table public.forums
(
    id                   serial
        primary key,
    name                 varchar(256)                                    not null,
    topic                varchar(255),
    description          text,
    created_by           integer                                         not null
        references public.users
            on delete cascade,
    university_domain_id integer                                         not null
        references public.university_domains
            on delete cascade,
    created_at           timestamp   default now(),
    status               varchar(50) default 'active'::character varying not null
        constraint forums_status_check
            check ((status)::text = ANY
                   (ARRAY [('active'::character varying)::text, ('closed'::character varying)::text])),
    is_pinned            boolean     default false,
    is_public            boolean     default true
);

alter table public.forums
    owner to admin;

create table public.events
(
    id          serial
        primary key,
    name        varchar(255) not null,
    description text,
    start_time  timestamp    not null,
    end_time    timestamp    not null,
    location    varchar(255),
    visibility  boolean   default true,
    created_at  timestamp default now(),
    created_by  integer      not null
        references public.users
            on delete cascade
);

alter table public.events
    owner to admin;

create table public.domain_events
(
    id        serial
        primary key,
    event_id  integer not null
        references public.events
            on delete cascade,
    domain_id integer not null
        references public.university_domains
            on delete cascade
);

alter table public.domain_events
    owner to admin;

create table public.group_events
(
    id       serial
        primary key,
    event_id integer not null
        references public.events
            on delete cascade,
    group_id integer not null
        references public.groups
            on delete cascade
);

alter table public.group_events
    owner to admin;

create table public.calendar
(
    id         serial
        primary key,
    user_id    integer      not null
        references public.users
            on delete cascade,
    name       varchar(255) not null,
    created_at timestamp default now()
);

alter table public.calendar
    owner to admin;

create table public.calendar_events
(
    id                   serial
        primary key,
    calendar_id          integer not null
        references public.calendar
            on delete cascade,
    event_id             integer not null
        references public.events
            on delete cascade,
    participation_status varchar(50),
    added_at             timestamp default now()
);

alter table public.calendar_events
    owner to admin;

create table public.user_details
(
    id            serial
        primary key,
    user_id       integer      not null
        references public.users
            on delete cascade,
    first_name    varchar(255) not null,
    last_name     varchar(255) not null,
    year_of_study integer,
    faculty       varchar(255),
    contacts      jsonb,
    created_at    timestamp default now(),
    updated_at    timestamp default now()
);

alter table public.user_details
    owner to admin;

create table public.logs
(
    id         serial
        primary key,
    user_id    integer
                            references public.users
                                on delete set null,
    log_type   varchar(255) not null,
    message    text,
    metadata   jsonb,
    created_at timestamp default now()
);

alter table public.logs
    owner to admin;

create table public.event_publication
(
    id               uuid not null
        primary key,
    completion_date  timestamp(6) with time zone,
    event_type       varchar(255),
    listener_id      varchar(255),
    publication_date timestamp(6) with time zone,
    serialized_event varchar(255)
);

alter table public.event_publication
    owner to admin;

