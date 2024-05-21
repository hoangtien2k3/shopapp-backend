SELECT COUNT(*)
INTO @columnCount
FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_NAME = 'tokens'
  AND TABLE_SCHEMA = 'shopapp'
  AND COLUMN_NAME = 'refresh_expiration_time';

-- if the column does not exist, add it
SET @alterStatement = IF(@columnCount = 0,
                         'ALTER TABLE tokens ADD COLUMN refresh_expiration_time TIMESTAMP;',
                         '');

-- Execute the ALTER TABLE statement if necessary
PREPARE stmt FROM @alterStatement;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;