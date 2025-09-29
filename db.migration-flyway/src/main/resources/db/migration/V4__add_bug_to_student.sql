ALTER TABLE student ADD COLUMN bug VARCHAR(255);

UPDATE student SET bug = 'none' WHERE name = 'Alice';
UPDATE student SET bug = 'typo' WHERE name = 'Bob';
UPDATE student SET bug = 'missing email' WHERE name = 'Charlie';