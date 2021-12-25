create schema stars;


create table stars.simulation(
    id varchar(255) primary key,
    owner varchar(255),
    email varchar(255)
);