
create table matchup (id varchar not null unique);
insert into matchup (id) values
  ('pvp'), ('pvt'), ('pvz'),
  ('tvp'), ('tvt'), ('tvz'),
  ('zvp'), ('zvt'), ('zvz'),
  ('pvx'), ('tvx'), ('zvx'),
  ('unknown');

create table build_type (id varchar not null unique);
insert into build_type (id) values
  ('allin'),
  ('cheese'),
  ('timingattack'),
  ('economic'),
  ('unknown');

create table build (
  id serial primary key,
  matchup varchar references matchup(id),
  duration integer not null,
  ttype varchar references build_type(id),
  patch varchar not null,
  author varchar not null,

  thumbs_up integer not null,
  thumbs_down integer not null,

  dictation_tg_id varchar default null
);

create table command (
  build_id integer references build(id),
  supply integer not null,
  when_do integer not null, -- seconds before game started
  what_do varchar not null  -- what to do
);

create table journal (
  user_id bigint not null,
  build_id integer references build(id),
  timestamp timestamp not null
);
