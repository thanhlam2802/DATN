CREATE TABLE otp_transactions
(
    id                BIGINT IDENTITY(1,1) PRIMARY KEY,
    otp_code          NVARCHAR(MAX),
    user_id           BIGINT,
    created_at        DATETIME2,
    expired_in_minute BIGINT,
    type              NVARCHAR(MAX),
);
