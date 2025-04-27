create sequence user_tokens_id_seq
    as integer;

alter sequence user_tokens_id_seq owner to admin;

create sequence university_domains_id_seq
    as integer;

alter sequence university_domains_id_seq owner to admin;

create sequence user_is_data_id_seq;

alter sequence user_is_data_id_seq owner to admin;

create table roles
(
    id   bigint generated always as identity
        constraint roles_pk
            primary key,
    name varchar(255) not null
);

alter table roles
    owner to admin;

create table university_domains
(
    id          bigint default nextval('university_domains_id_seq'::regclass) not null
        primary key,
    domain_name varchar(255)                                                  not null,
    domain      varchar(255)                                                  not null,
    admin_email varchar(255)                                                  not null,
    website_url varchar(255)                                                  not null
);

alter table university_domains
    owner to admin;

alter sequence university_domains_id_seq owned by university_domains.id;

create table users
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
    domain_id            bigint
        constraint fk_users_domains
            references university_domains
            on delete set null,
    verification_code    varchar(6)
        constraint chk_user_verification_code_length
            check ((verification_code)::text ~ '^[0-9]{6}$'::text),
    verification_expires timestamp,
    details              jsonb
);

comment on column users.details is 'Extended user profile data:
{
  "bio": "Short description of the profile",
  "interests": ["technology", "football", "reading"],
  "birthdate": "1995-06-12",
  "languages": ["cs", "ru", "en"],
  "contacts": {
    "inst": "username_insta",
    "tg": "@username",
    "fb": "facebook.com/user",
    "steam": "steamcommunity.com/id/user",
    "ln": "linkedin.com/in/user",
    "telephone": "+420123456789",
    "other": [
      {"source": "discord", "value": "user#1234"},
      {"source": "matrix", "value": "@user:matrix.org"}
    ]
  },
  "location": "Prague, Czech Republic",
  "website": "https://mywebsite.com",
  "status": "Open to conversation",
  "skills": ["Vue", "Java", "SQL"]
}';

alter table users
    owner to admin;

create table user_roles
(
    user_id bigint not null
        references users
            on delete cascade,
    role_id bigint not null
        references roles
            on delete cascade,
    primary key (user_id, role_id)
);

alter table user_roles
    owner to admin;

create table user_tokens
(
    id           bigint       default nextval('user_tokens_id_seq'::regclass) not null
        primary key,
    user_id      bigint                                                       not null
        references users
            on delete cascade,
    token        varchar(255)                                                 not null,
    token_type   varchar(255)                                                 not null
        constraint user_tokens_token_type_check
            check ((token_type)::text = ANY
                   (ARRAY [('auth'::character varying)::text, ('refresh'::character varying)::text, ('uni'::character varying)::text])),
    expiration   timestamp                                                    not null,
    created_at   timestamp    default now(),
    token_origin varchar(255) default 'local'::character varying              not null
        constraint user_tokens_token_origin_check
            check ((token_origin)::text = ANY
                   (ARRAY [('local'::character varying)::text, ('stag'::character varying)::text]))
);

alter table user_tokens
    owner to admin;

alter sequence user_tokens_id_seq owned by user_tokens.id;

create index idx_user_tokens_token
    on user_tokens (token);

create index idx_user_tokens_user_id
    on user_tokens (user_id);

create table groups
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
                   (ARRAY [('owner'::character varying)::text, ('admin'::character varying)::text, ('editor'::character varying)::text, ('member'::character varying)::text])),
    domain_id           bigint
                                     references university_domains
                                         on delete set null
);

alter table groups
    owner to admin;

create index idx_groups_domain_id
    on groups (domain_id);

create table group_announcements
(
    id         serial
        primary key,
    group_id   integer      not null
        references groups
            on delete cascade,
    title      varchar(255) not null,
    content    text         not null,
    created_by integer      not null
        references users
            on delete cascade,
    created_at timestamp default now()
);

alter table group_announcements
    owner to admin;

create table group_attachments
(
    id          serial
        primary key,
    group_id    integer      not null
        references groups
            on delete cascade,
    uploaded_by integer      not null
        references users
            on delete cascade,
    file_url    varchar(255) not null,
    uploaded_at timestamp default now()
);

alter table group_attachments
    owner to admin;

create table user_group_memberships
(
    id        serial
        primary key,
    user_id   integer     not null
        references users
            on delete cascade,
    group_id  integer     not null
        references groups
            on delete cascade,
    status    varchar(50) not null
        constraint user_group_memberships_status_check
            check ((status)::text = ANY
                   (ARRAY [('approved'::character varying)::text, ('pending'::character varying)::text, ('banned'::character varying)::text])),
    role      varchar(50) not null
        constraint user_group_memberships_role_check
            check ((role)::text = ANY
                   (ARRAY [('owner'::character varying)::text, ('admin'::character varying)::text, ('editor'::character varying)::text, ('member'::character varying)::text, ('invited'::character varying)::text, ('helper'::character varying)::text])),
    joined_at timestamp default now(),
    constraint unique_user_in_group
        unique (user_id, group_id)
);

alter table user_group_memberships
    owner to admin;

create table sale_items
(
    id          serial
        primary key,
    title       varchar(255)                                    not null,
    description text,
    price       numeric(10, 2),
    topics      jsonb,
    created_at  timestamp   default now(),
    created_by  integer                                         not null
        references users
            on delete cascade,
    status      varchar(50) default 'active'::character varying not null
        constraint sale_items_status_check
            check ((status)::text = ANY
                   (ARRAY [('active'::character varying)::text, ('closed'::character varying)::text])),
    images      jsonb,
    is_local    boolean     default false
);

alter table sale_items
    owner to admin;

create table forum_posts
(
    id             serial
        primary key,
    forum_id       integer not null,
    author_id      integer not null
        references users
            on delete cascade,
    content        text    not null,
    created_at     timestamp default now(),
    updated_at     timestamp,
    parent_post_id integer
                           references forum_posts
                               on delete set null
);

alter table forum_posts
    owner to admin;

create table forums
(
    id                   serial
        primary key,
    name                 varchar(256)                                    not null,
    topics               jsonb       default '[]'::jsonb,
    description          text,
    created_by           integer                                         not null
        references users
            on delete cascade,
    university_domain_id integer
        references university_domains
            on delete cascade,
    created_at           timestamp   default now()                       not null,
    status               varchar(50) default 'active'::character varying not null,
    is_pinned            boolean     default false                       not null,
    is_public            boolean     default true                        not null,
    is_closed            boolean     default false                       not null
);

alter table forums
    owner to admin;

create table events
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
        references users
            on delete cascade
);

alter table events
    owner to admin;

create table domain_events
(
    id        serial
        primary key,
    event_id  integer not null
        references events
            on delete cascade,
    domain_id integer not null
        references university_domains
            on delete cascade
);

alter table domain_events
    owner to admin;

create table group_events
(
    id       serial
        primary key,
    event_id integer not null
        references events
            on delete cascade,
    group_id integer not null
        references groups
            on delete cascade
);

alter table group_events
    owner to admin;

create table calendar
(
    id         serial
        primary key,
    user_id    integer      not null
        references users
            on delete cascade,
    name       varchar(255) not null,
    created_at timestamp default now()
);

alter table calendar
    owner to admin;

create table calendar_events
(
    id                   serial
        primary key,
    calendar_id          integer not null
        references calendar
            on delete cascade,
    event_id             integer not null
        references events
            on delete cascade,
    participation_status varchar(50),
    added_at             timestamp default now()
);

alter table calendar_events
    owner to admin;

create table logs
(
    id         serial
        primary key,
    user_id    integer
                            references users
                                on delete set null,
    log_type   varchar(255) not null,
    message    text,
    metadata   jsonb,
    created_at timestamp default now()
);

alter table logs
    owner to admin;

create table event_publication
(
    id               uuid not null
        primary key,
    completion_date  timestamp(6) with time zone,
    event_type       varchar(255),
    listener_id      varchar(255),
    publication_date timestamp(6) with time zone,
    serialized_event varchar(255)
);

alter table event_publication
    owner to admin;

create table user_is_data
(
    id                     bigint    default nextval('user_is_data_id_seq'::regclass) not null
        primary key,
    user_id                bigint                                                     not null
        references users
            on delete cascade,
    os_cislo               varchar(255),
    stpr_idno              varchar(255),
    user_name              varchar(255),
    email                  varchar(255),
    jmeno                  varchar(255),
    prijmeni               varchar(255),
    titul_pred             varchar(255),
    titul_za               varchar(255),
    pohlavi                varchar(255),
    obor_komb              varchar(255),
    nazev_sp               varchar(255),
    kod_sp                 varchar(255),
    fakulta_sp             varchar(255),
    forma_sp               varchar(255),
    typ_sp                 varchar(255),
    rocnik                 varchar(255),
    stav                   varchar(255),
    misto_vyuky            varchar(255),
    cislo_karty            varchar(255),
    rozvrhovy_krouzek      varchar(255),
    studijni_kruh          varchar(255),
    evidovan_bankovni_ucet varchar(255),
    created_at             timestamp default now(),
    updated_at             timestamp default now()
);

alter table user_is_data
    owner to admin;

create table user_friends
(
    user_id_1  bigint                                           not null
        constraint event_publication_pkey
            primary key
        references users
            on delete cascade,
    user_id_2  bigint                                           not null
        references users
            on delete cascade,
    status     varchar(20) default 'pending'::character varying not null
        constraint user_friends_status_check
            check ((status)::text = ANY
                   (ARRAY [('pending'::character varying)::text, ('approved'::character varying)::text])),
    hidden     boolean     default false,
    created_at timestamp   default now(),
    primary key (user_id_1, user_id_2)
);

alter table user_friends
    owner to admin;

create table group_posts
(
    id         serial
        primary key,
    group_id   integer                       not null
        references groups
            on delete cascade,
    user_id    integer                       not null
        references users
            on delete cascade,
    title      varchar(255)                  not null,
    topics     jsonb     default '[]'::jsonb not null,
    content    text                          not null,
    created_at timestamp default now(),
    updated_at timestamp
);

alter table group_posts
    owner to admin;

create table forum_allowed_roles
(
    forum_id integer not null
        references forums
            on delete cascade,
    role_id  bigint  not null
        references roles
            on delete cascade,
    primary key (forum_id, role_id)
);

alter table forum_allowed_roles
    owner to admin;

create index idx_forum_allowed_roles_role_id
    on forum_allowed_roles (role_id);

create view user_profile_view
            (user_id, email, active, domain, admin_email, os_cislo, stpr_idno, user_name, jmeno, prijmeni, titul_pred,
             titul_za, pohlavi, fakulta_sp, obor_komb, nazev_sp, kod_sp, forma_sp, typ_sp, rocnik, stav, misto_vyuky,
             cislo_karty, rozvrhovy_krouzek, studijni_kruh, evidovan_bankovni_ucet, details)
as
SELECT u.id AS user_id,
       u.email,
       u.active,
       d.domain,
       d.admin_email,
       isd.os_cislo,
       isd.stpr_idno,
       isd.user_name,
       isd.jmeno,
       isd.prijmeni,
       isd.titul_pred,
       isd.titul_za,
       isd.pohlavi,
       isd.fakulta_sp,
       isd.obor_komb,
       isd.nazev_sp,
       isd.kod_sp,
       isd.forma_sp,
       isd.typ_sp,
       isd.rocnik,
       isd.stav,
       isd.misto_vyuky,
       isd.cislo_karty,
       isd.rozvrhovy_krouzek,
       isd.studijni_kruh,
       isd.evidovan_bankovni_ucet,
       u.details
FROM users u
         LEFT JOIN university_domains d ON u.domain_id = d.id
         LEFT JOIN user_is_data isd ON u.id = isd.user_id;

alter table user_profile_view
    owner to admin;

create view pending_friend_requests
            (requester_id, requester_email, recipient_id, recipient_email, status, hidden, created_at) as
SELECT uf.user_id_1 AS requester_id,
       u1.email     AS requester_email,
       uf.user_id_2 AS recipient_id,
       u2.email     AS recipient_email,
       uf.status,
       uf.hidden,
       uf.created_at
FROM user_friends uf
         JOIN users u1 ON u1.id = uf.user_id_1
         JOIN users u2 ON u2.id = uf.user_id_2
WHERE uf.status::text = 'pending'::text;

alter table pending_friend_requests
    owner to admin;

