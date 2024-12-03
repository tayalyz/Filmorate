MERGE INTO genres (id, name)
    VALUES (1, 'Комедия'),
           (2, 'Драма'),
           (3, 'Мультфильм'),
           (4, 'Триллер'),
           (5, 'Документальный'),
           (6, 'Боевик');

MERGE INTO mpa_rating (id, name)
    VALUES (1, 'G'),
           (2, 'PG'),
           (3, 'PG-13'),
           (4, 'R'),
           (5, 'NC-17');

INSERT INTO FILMS(NAME, DESCRIPTION, RELEASE_DATE, DURATION) VALUES ('NAME', 'DESC', '2019-12-12', 100);
INSERT INTO FILMS(NAME, DESCRIPTION, RELEASE_DATE, DURATION) VALUES ('NAME2', 'DESC2', '2012-10-01', 100);
INSERT INTO FILMS(NAME, DESCRIPTION, RELEASE_DATE, DURATION) VALUES ('NAME3', 'DESC3', '2001-01-01', 110);

INSERT INTO USERS(EMAIL, LOGIN, NAME, BIRTHDAY) VALUES ('u1@gmail.com', 'LOGIN1', 'NAME1', '2019-12-12');
INSERT INTO USERS(EMAIL, LOGIN, NAME, BIRTHDAY) VALUES ('u2@gmail.com', 'LOGIN2', 'NAME2', '2000-01-07');
INSERT INTO USERS(EMAIL, LOGIN, NAME, BIRTHDAY) VALUES ('u3@gmail.com', 'LOGIN3', 'NAME3', '2003-03-03');
