--liquibase formatted sql
--changeset jungkaew:JRA-2222_1 labels:sprint-11
--comment: add more students
INSERT INTO student (name) VALUES ('Bob');
INSERT INTO student (name) VALUES ('Charlie');
--rollback DELETE FROM student WHERE name IN ('Bob', 'Charlie');