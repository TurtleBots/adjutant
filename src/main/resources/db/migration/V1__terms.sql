
create type matchup as enum (
  'pvp', 'pvt', 'pvz',
  'tvp', 'tvt', 'tvz',
  'zvp', 'zvt', 'zvz',
  'pvx', 'tvx', 'zvx'
);

create table build (
  id varchar(20) primary key,
  matchup matchup not null,
  duration integer not null,
  type varchar not null,

  thumbs_up integer not null,
  thumbs_down integer not null,

  dictation_file varchar,
  dictation_tg_id varchar
);

create table command (
  build_id varchar(20) references build(id),
  supply integer not null,
  when_do integer not null, -- seconds before game started
  what_do varchar not null  -- what to do
);

create table journal (
  user_id bigint not null,
  build_id varchar(20) references build(id),
  timestamp timestamp not null
);
