create database if not exists milou;
use milou;

create table if not exists users (
  id int primary key auto_increment,
  name nvarchar(255) not null,
  email nvarchar(255) not null unique,
  password_hash nvarchar(255) not null,
  created_at datetime not null default current_timestamp
);

create table if not exists emails (
  id int primary key auto_increment,
  code nvarchar(6) not null unique,
  sender_id int not null,
  subject nvarchar(255) not null,
  body text not null,
  sent_at datetime not null default current_timestamp,
  constraint foreign key (sender_id) references users(id)
);

create table if not exists email_recipients (
  email_id int not null,
  user_id int not null,
  is_read boolean not null default false,
  read_at datetime null,
  primary key (email_id, user_id),
  constraint foreign key (email_id) references emails(id),
  constraint foreign key (user_id) references users(id)
);