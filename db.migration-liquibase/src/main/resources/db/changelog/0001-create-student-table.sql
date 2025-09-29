--liquibase formatted sql
--changeset jungkaew:create-student-table
CREATE TABLE student (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL
);
--rollback DROP TABLE
--rollback student
