create table employee (id varchar(255) not null, login varchar(255), name varchar(255), salary decimal(19,2), start_date date, primary key (id))
alter table employee drop constraint if exists UKaju94j50rv740vc03wkcggs97
alter table employee add constraint UKaju94j50rv740vc03wkcggs97 unique (login)