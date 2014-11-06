# --- !Ups

-- stores for mapping url(s) -> shrt(s)
create table shrts (
  id               bigint primary key auto_increment,
  keyword          varchar_ignorecase not null,
  url              varchar_ignorecase not null,
  token            varchar_ignorecase not null,
  description      varchar,
  tags             varchar_ignorecase,
  count            bigint not null default 0,
  created_at       timestamp not null default now(),
  deleted_at       timestamp,
  is_deleted       boolean default false
);

# --- !Downs
drop table shrts;
