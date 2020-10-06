create table users(
    id serial primary key,
    name text not null,
    surname text not null,
    date_of_birth timestamp not null,
    address text not null
);

insert into users (name, surname, date_of_birth, address) values
('Artem', 'Avvakumov', '1989-09-01T12:00:00', 'St.Petersburg'),
('Random', 'Testovich', '1989-09-01T12:00:00', 'St.Petersburg')
;