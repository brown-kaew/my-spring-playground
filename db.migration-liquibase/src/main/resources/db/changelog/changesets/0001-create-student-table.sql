--liquibase formatted sql
--changeset jungkaew:JRA-1111_1 labels:sprint-10
--comment: create student table
CREATE TABLE student (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL
);
--rollback DROP TABLE student;

--liquibase formatted sql
--changeset jungkaew:JRA-1111_2 labels:sprint-10
--comment: add student
INSERT INTO student (name) VALUES ('Alice');
---rollback DELETE FROM student WHERE name = 'Alice';
