SET GLOBAL log_bin_trust_function_creators = 1;

DROP TABLE IF EXISTS my_user;
CREATE TABLE IF NOT EXISTS my_user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL
);

CREATE TABLE sequence
(
    name      VARCHAR(255)      NOT NULL,
    cur_val   BIGINT UNSIGNED   NOT NULL DEFAULT 0,
    increment SMALLINT UNSIGNED NOT NULL DEFAULT 1,
    max_val   BIGINT UNSIGNED   NOT NULL DEFAULT 9223372036854775807,
    PRIMARY KEY (name)
);

DELIMITER //
CREATE FUNCTION next_val (seq_name VARCHAR(255))
RETURNS BIGINT UNSIGNED NOT DETERMINISTIC
BEGIN
    DECLARE next BIGINT UNSIGNED;

    UPDATE sequence
    SET cur_val = last_insert_id((cur_val + increment) % max_val)
    WHERE name = seq_name;

    SELECT last_insert_id() AS cur_val INTO next;

    RETURN next;
END//
DELIMITER ;

CREATE TABLE seq_1_to_1000
(
    id INT UNSIGNED NOT NULL PRIMARY KEY
);

INSERT INTO seq_1_to_1000 (id)
SELECT * FROM
(
	WITH RECURSIVE numbers (n) AS (
	  SELECT 1
	  UNION ALL
	  SELECT n + 1 FROM numbers WHERE n < 1000
	)SELECT n FROM numbers
) AS numbers;
