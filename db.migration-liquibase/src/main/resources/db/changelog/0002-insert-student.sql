--liquibase formatted sql
--changeset jungkaew:insert-student
INSERT INTO student (name) VALUES ('Alice');
INSERT INTO student (name) VALUES ('Bob');
INSERT INTO student (name) VALUES ('Charlie');
--rollback DELETE FROM
--rollback student