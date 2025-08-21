-- =============================================
-- SCRIPT: Fix routes owner_id data
-- Cập nhật owner_id cho routes hiện có
-- =============================================

-- 1. Kiểm tra routes chưa có owner_id
SELECT 'Routes without owner_id:' as check_type, COUNT(*) as count 
FROM routes WHERE owner_id IS NULL;

-- 2. Kiểm tra users có sẵn
SELECT 'Available users:' as check_type, COUNT(*) as count 
FROM users;

-- 3. Hiển thị các users để chọn
SELECT id, name, email FROM users ORDER BY id;

-- 4. Cập nhật routes chưa có owner_id
-- Gán cho user ID = 11 (hoặc thay đổi ID phù hợp)
UPDATE routes 
SET owner_id = 11 
WHERE owner_id IS NULL;

-- 5. Kiểm tra sau khi cập nhật
SELECT 'Routes after update:' as check_type, 
       COUNT(*) as total_routes,
       COUNT(CASE WHEN owner_id IS NOT NULL THEN 1 END) as routes_with_owner,
       COUNT(CASE WHEN owner_id IS NULL THEN 1 END) as routes_without_owner
FROM routes;

-- 6. Hiển thị phân bổ theo owner
SELECT owner_id, COUNT(*) as route_count
FROM routes 
WHERE owner_id IS NOT NULL
GROUP BY owner_id
ORDER BY owner_id;
