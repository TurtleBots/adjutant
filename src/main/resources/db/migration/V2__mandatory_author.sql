
alter table build
drop column author;

alter table build
add column author bigint not null default 108683062;
