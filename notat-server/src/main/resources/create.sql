create table if not exists versjon (
  id int not null auto_increment primary key,
  versjon_nr varchar(255) not null
);

create table if not exists gruppe (
  id int not null auto_increment primary key,
  gruppenavn varchar(255) not null
);


create table if not exists notat (
  id int not null auto_increment primary key,
  gruppe_id int not null,
  tittel varchar(255) not null,
  innhold text,
  endret_tid datetime not null,
  foreign key (gruppe_id) references gruppe (id)
);