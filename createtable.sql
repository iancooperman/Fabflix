CREATE DATABASE moviedb
    CHARACTER SET utf8;

USE moviedb;

CREATE TABLE movies (
    id VARCHAR(10) NOT NULL PRIMARY KEY,
    title VARCHAR(100) NOT NULL,
    year INTEGER NOT NULL,
    director VARCHAR(100) NOT NULL,
    FULLTEXT (title)
);

CREATE TABLE stars (
    id VARCHAR(10) NOT NULL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    birthYear INTEGER
);

CREATE TABLE stars_in_movies (
    starId VARCHAR(10) NOT NULL,
    FOREIGN KEY(starID) REFERENCES stars(id),
    movieId VARCHAR(10) NOT NULL,
    FOREIGN KEY(movieId) REFERENCES movies(id)
);

CREATE TABLE genres (
    id INTEGER AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(32) NOT NULL
);

CREATE TABLE genres_in_movies (
    genreId INTEGER NOT NULL,
    FOREIGN KEY(genreId) REFERENCES genres(id),
    movieId VARCHAR(10) NOT NULL,
    FOREIGN KEY(movieId) REFERENCES movies(id)
);

CREATE TABLE creditcards (
    id VARCHAR(20) NOT NULL PRIMARY KEY,
    firstName VARCHAR(50) NOT NULL,
    lastName VARCHAR(50) NOT NULL,
    expiration DATE NOT NULL
);

CREATE TABLE customers (
    id INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,
    firstName VARCHAR(50) NOT NULL,
    lastName VARCHAR(50) NOT NULL,
    ccId VARCHAR(20) NOT NULL,
    FOREIGN KEY(ccId) REFERENCES creditcards(id),
    address VARCHAR(200) NOT NULL,
    email VARCHAR(50) NOT NULL,
    password VARCHAR(20) NOT NULL
);

CREATE TABLE sales (
    id INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,
    customerID INTEGER NOT NULL,
    FOREIGN KEY(customerID) REFERENCES customers(id),
    movieId VARCHAR(10) NOT NULL,
    FOREIGN KEY(movieId) REFERENCES movies(id),
    saleDate DATE NOT NULL
);


CREATE TABLE ratings (
    movieId VARCHAR(10) NOT NULL,
    FOREIGN KEY(movieId) REFERENCES movies(id),
    rating FLOAT NOT NULL,
    numVotes INTEGER NOT NULL
);

CREATE TABLE employees (
    email VARCHAR(50) PRIMARY KEY,
    password VARCHAR(20) NOT NULL,
    fullname VARCHAR(100)
);