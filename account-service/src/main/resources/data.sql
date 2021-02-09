/**
 * CREATE Script for init of DB
 */

INSERT INTO customer (id, first_name, surname, email, date_created)
VALUES (1, 'John', 'Doe', 'johndoe@example.com', NOW());

INSERT INTO customer (id, first_name, surname, email, date_created)
VALUES (2, 'Oscar', 'Keller', 'oscarkeller@example.com', NOW());

INSERT INTO customer (id, first_name, surname, email, date_created)
VALUES (3, 'Lois', 'Harrison', 'loisharrison@example.com', NOW());

INSERT INTO customer (id, first_name, surname, email, date_created)
VALUES (4, 'Barnaby', 'Bennett', 'barnabybennett@example.com', NOW());

INSERT INTO customer (id, first_name, surname, email, date_created)
VALUES (5, 'Mona', 'Tate', 'monatate@example.com', NOW());
