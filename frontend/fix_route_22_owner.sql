-- =============================================
-- FIX: Update route ID 22 với owner_id = 45
-- =============================================

-- 1. Kiểm tra route hiện tại
SELECT id, owner_id, created_at FROM routes WHERE id = 22;

-- 2. Kiểm tra user ID 45 có tồn tại không
SELECT id, name, email FROM users WHERE id = 45;

-- 3. Update route 22 với owner_id = 45
UPDATE routes SET owner_id = 45 WHERE id = 22;

-- 4. Kiểm tra sau update
SELECT 
    r.id as route_id,
    r.owner_id,
    u.name as owner_name,
    u.email as owner_email
FROM routes r
LEFT JOIN users u ON r.owner_id = u.id 
WHERE r.id = 22;
