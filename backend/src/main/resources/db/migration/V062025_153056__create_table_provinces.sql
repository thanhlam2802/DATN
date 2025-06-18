-- Migration file created on 18/06/2025 at 15:30:56.79 
CREATE TABLE provinces (
    id INT IDENTITY(1,1) PRIMARY KEY,
    name VARCHAR(100) NOT NULL
);