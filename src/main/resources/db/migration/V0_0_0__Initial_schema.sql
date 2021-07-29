CREATE SCHEMA IF NOT EXISTS bookstore;

CREATE TYPE bookstore.book_type AS ENUM (
    'NEW_RELEASE',
    'REGULAR',
    'OLD_EDITION'
    );

CREATE TABLE bookstore.book
(
    id             BIGSERIAL PRIMARY KEY,
    name           text                NOT NULL,
    published_date DATE                NOT NULL,
    price          DECIMAL             NOT NULL,
    type           bookstore.book_type NOT NULL,

    CONSTRAINT unique_book_name_published_date UNIQUE (name, published_date)
);

CREATE TABLE bookstore.author
(
    id         BIGSERIAL PRIMARY KEY,
    name       text NOT NULL,
    last_name  text,
    birth_date DATE NOT NULL,

    CONSTRAINT unique_author_name_last_name_birth_date UNIQUE (name, last_name, birth_date)
);

CREATE TABLE bookstore.author_book
(
    author_id BIGINT NOT NULL,
    book_id   BIGINT NOT NULL,

    CONSTRAINT author_id_foreign_key FOREIGN KEY (author_id) REFERENCES bookstore.author,
    CONSTRAINT book_id_foreign_key FOREIGN KEY (book_id) REFERENCES bookstore.book,
    CONSTRAINT unique_author_book UNIQUE (author_id, book_id)
);

CREATE TABLE bookstore.user
(
    id             BIGSERIAL PRIMARY KEY,
    username       text NOT NULL UNIQUE,
    loyalty_points INT DEFAULT 0
);

CREATE TABLE bookstore.purchase
(
    user_id BIGINT  NOT NULL,
    book_id BIGINT  NOT NULL,
    price   DECIMAL NOT NULL,

    CONSTRAINT user_id_foreign_key FOREIGN KEY (user_id) REFERENCES bookstore.user,
    CONSTRAINT book_id_foreign_key FOREIGN KEY (book_id) REFERENCES bookstore.book
);

------ INSERT INITIAL DATA

INSERT INTO bookstore.book (name, published_date, price, type)
VALUES ('Catch 22', '1961-11-10'::DATE, 100, 'NEW_RELEASE'),
       ('Three Musketeers', '1844-04-01'::DATE, 100, 'OLD_EDITION');

INSERT INTO bookstore.author(name, last_name, birth_date)
VALUES ('Joseph', 'Heller', '1923-05-01'::DATE),
       ('Alexandre', 'Dumas', '1802-02-24'::DATE);

INSERT INTO bookstore.author_book(author_id, book_id)
SELECT a.id, b.id
FROM bookstore.author a JOIN bookstore.book b ON TRUE
WHERE (a.name = 'Joseph' AND b.name = 'Catch 22') OR (a.name = 'Alexandre' AND b.name = 'Three Musketeers');