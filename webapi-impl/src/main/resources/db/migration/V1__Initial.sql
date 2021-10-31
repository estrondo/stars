create schema stars;


create table stars.simulation(
    id uuid primary key,
    owner varchar(255),
    email varchar(255)
);