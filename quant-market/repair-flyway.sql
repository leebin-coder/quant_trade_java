-- Repair Flyway Migration History
-- Execute this SQL in your PostgreSQL database client (DBeaver, pgAdmin, etc.)

-- Option 1: Delete all migration history and let Flyway re-initialize
DELETE FROM flyway_schema_history;

-- Option 2: Only delete specific versions (if you want to keep some history)
-- DELETE FROM flyway_schema_history WHERE version IN ('1', '2');

-- After running this SQL, restart your Spring Boot application
