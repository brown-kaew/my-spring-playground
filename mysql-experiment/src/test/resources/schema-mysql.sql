SET GLOBAL log_bin_trust_function_creators = 1;

DROP TABLE IF EXISTS my_user;
CREATE TABLE IF NOT EXISTS my_user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS sequences
(
    name      VARCHAR(255)      NOT NULL,
    current_value   BIGINT UNSIGNED   NOT NULL DEFAULT 0,
    increment SMALLINT UNSIGNED NOT NULL DEFAULT 1,
    max_value BIGINT UNSIGNED NOT NULL DEFAULT 18446744073709551615,
    PRIMARY KEY (name)
);

DELIMITER //
CREATE FUNCTION IF NOT EXISTS next_value (seq_name VARCHAR(255))
RETURNS BIGINT UNSIGNED NOT DETERMINISTIC
BEGIN
    DECLARE next BIGINT UNSIGNED;

    UPDATE sequences
    SET current_value = last_insert_id(((current_value) % max_value) + increment)
    WHERE name = seq_name;

    SELECT last_insert_id() AS current_value
    INTO next;

    RETURN next - 1;
END//
DELIMITER ;