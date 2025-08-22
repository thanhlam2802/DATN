-- =============================================
-- MIGRATION: Thêm owner_id vào bảng routes
-- Version: 1.0
-- Safe Migration với backup và rollback
-- =============================================

-- BƯỚC 0: Kiểm tra môi trường
SELECT 'BEFORE MIGRATION - Current routes count:' as step, COUNT(*) as count FROM routes;
SELECT 'BEFORE MIGRATION - Current users count:' as step, COUNT(*) as count FROM users;

-- BƯỚC 1: Backup bảng routes (tùy chọn)
-- CREATE TABLE routes_backup_20250817 AS SELECT * FROM routes;

-- BƯỚC 2: Kiểm tra xem cột owner_id đã tồn tại chưa
SELECT 
    CASE 
        WHEN COUNT(*) > 0 THEN 'Column owner_id already exists - SKIP MIGRATION'
        ELSE 'Column owner_id not found - PROCEED WITH MIGRATION'
    END as migration_status
FROM INFORMATION_SCHEMA.COLUMNS 
WHERE TABLE_NAME = 'routes' 
AND COLUMN_NAME = 'owner_id';

-- BƯỚC 3: Thêm cột owner_id (chỉ chạy nếu chưa tồn tại)
-- Uncomment dòng dưới để thực thi:
-- ALTER TABLE routes ADD COLUMN owner_id BIGINT;

-- BƯỚC 4: Kiểm tra có user nào trong hệ thống không
SELECT 
    CASE 
        WHEN COUNT(*) = 0 THEN 'NO USERS FOUND - CREATE DEFAULT USER FIRST'
        ELSE CONCAT('Found ', COUNT(*), ' users - OK to proceed')
    END as user_check,
    MIN(id) as first_user_id
FROM users;

-- BƯỚC 5: Cập nhật owner_id cho routes hiện có
-- Strategy 1: Gán tất cả cho user đầu tiên
-- UPDATE routes SET owner_id = (SELECT MIN(id) FROM users) WHERE owner_id IS NULL;

-- Strategy 2: Phân bổ random cho các users
-- UPDATE routes SET owner_id = (
--     SELECT id FROM users 
--     ORDER BY RAND() -- MySQL: RAND(), PostgreSQL: RANDOM()
--     LIMIT 1
-- ) WHERE owner_id IS NULL;

-- Strategy 3: Gán cho user cụ thể (ID = 11 như trong code)
-- UPDATE routes SET owner_id = 11 WHERE owner_id IS NULL;

-- BƯỚC 6: Kiểm tra sau khi cập nhật
-- SELECT 'AFTER UPDATE - Routes with owner_id:' as step, COUNT(*) as count FROM routes WHERE owner_id IS NOT NULL;
-- SELECT 'AFTER UPDATE - Routes without owner_id:' as step, COUNT(*) as count FROM routes WHERE owner_id IS NULL;

-- BƯỚC 7: Thêm constraints (chỉ sau khi tất cả routes đã có owner_id)
-- ALTER TABLE routes MODIFY COLUMN owner_id BIGINT NOT NULL;
-- ALTER TABLE routes ADD CONSTRAINT fk_routes_owner_id FOREIGN KEY (owner_id) REFERENCES users(id) ON DELETE CASCADE;
-- CREATE INDEX idx_routes_owner_id ON routes(owner_id);

-- BƯỚC 8: Kiểm tra kết quả cuối cùng
-- SELECT 
--     'FINAL CHECK' as step,
--     COUNT(*) as total_routes,
--     COUNT(DISTINCT owner_id) as unique_owners,
--     MIN(owner_id) as min_owner_id,
--     MAX(owner_id) as max_owner_id
-- FROM routes;

-- ROLLBACK SCRIPT (nếu cần):
-- ALTER TABLE routes DROP FOREIGN KEY fk_routes_owner_id;
-- DROP INDEX idx_routes_owner_id ON routes;
-- ALTER TABLE routes DROP COLUMN owner_id;
-- DROP TABLE routes_backup_20250817;

-- =============================================
-- HƯỚNG DẪN SỬ DỤNG:
-- =============================================
-- 1. Chạy các SELECT statement trước để kiểm tra
-- 2. Uncomment từng ALTER/UPDATE statement theo thứ tự
-- 3. Kiểm tra kết quả sau mỗi bước
-- 4. Backup database trước khi thực hiện
-- =============================================
