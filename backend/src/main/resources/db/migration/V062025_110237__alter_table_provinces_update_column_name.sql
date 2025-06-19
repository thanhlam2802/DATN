-- Migration file created on 19/06/2025 at 11:02:37.19 
ALTER TABLE provinces
ALTER COLUMN name NVARCHAR(100) NOT NULL;

