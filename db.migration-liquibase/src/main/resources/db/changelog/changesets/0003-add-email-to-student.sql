--liquibase formatted sql
--changeset jungkaew:JRA-3333_1 labels:sprint-12
--comment: add email column
ALTER TABLE student ADD COLUMN email VARCHAR(255);
--rollback ALTER TABLE student DROP COLUMN email;


--liquibase formatted sql
--changeset jungkaew:JRA-3333_2 labels:sprint-12
--comment: update email for existing students
UPDATE student SET email = 'alice@example.com' WHERE name = 'Alice';
UPDATE student SET email = 'bob@example.com' WHERE name = 'Bob';
UPDATE student SET email = 'charlie@example.com' WHERE name = 'Charlie';
--rollback UPDATE student SET email = NULL WHERE name IN ('Alice', 'Bob', 'Charlie');
