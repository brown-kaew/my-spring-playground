ALTER TABLE student ADD COLUMN email VARCHAR(255);

UPDATE student SET email = 'alice@example.com' WHERE name = 'Alice';
UPDATE student SET email = 'bob@example.com' WHERE name = 'Bob';
UPDATE student SET email = 'charlie@example.com' WHERE name = 'Charlie';

