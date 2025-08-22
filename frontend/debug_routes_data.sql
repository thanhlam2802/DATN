-- =============================================
-- DEBUG: Kiểm tra data routes và owner_id
-- =============================================

-- 1. Kiểm tra structure bảng routes
DESCRIBE routes;

-- 2. Kiểm tra data routes hiện tại
SELECT 
    id,
    owner_id,
    created_at,
    updated_at
FROM routes;

-- 3. Kiểm tra routes với thông tin owner
SELECT 
    r.id as route_id,
    r.owner_id,
    u.id as user_id,
    u.name as user_name,
    u.email as user_email
FROM routes r
LEFT JOIN users u ON r.owner_id = u.id;

-- 4. Kiểm tra routes có owner_id NULL
SELECT 
    'Routes with NULL owner_id:' as check_type,
    COUNT(*) as count
FROM routes 
WHERE owner_id IS NULL;

-- 5. Kiểm tra routes có owner_id NOT NULL
SELECT 
    'Routes with owner_id:' as check_type,
    COUNT(*) as count
FROM routes 
WHERE owner_id IS NOT NULL;

-- 6. Nếu có routes NULL, update chúng
-- UNCOMMENT để chạy:
-- UPDATE routes SET owner_id = 11 WHERE owner_id IS NULL;

-- 7. Kiểm tra lại sau update
-- SELECT 'After update - Routes status:' as step;
-- SELECT owner_id, COUNT(*) as route_count FROM routes GROUP BY owner_id;
