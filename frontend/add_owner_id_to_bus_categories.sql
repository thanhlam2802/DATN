-- =============================================
-- SCRIPT: Thêm owner_id vào bảng bus_categories
-- Mục đích: BusCategory thuộc về doanh nghiệp
-- =============================================

-- 1. Thêm cột owner_id vào bảng bus_categories
ALTER TABLE bus_categories 
ADD owner_id BIGINT;

-- 2. Cập nhật dữ liệu hiện có
-- Gán tất cả categories hiện tại cho user có id = 1 (hoặc admin user)
UPDATE bus_categories 
SET owner_id = 1 
WHERE owner_id IS NULL;

-- 3. Thêm ràng buộc NOT NULL
ALTER TABLE bus_categories 
ALTER COLUMN owner_id BIGINT NOT NULL;

-- 4. Thêm foreign key constraint
ALTER TABLE bus_categories 
ADD CONSTRAINT FK_bus_categories_owner_id 
FOREIGN KEY (owner_id) REFERENCES users(id) 
ON DELETE CASCADE 
ON UPDATE CASCADE;

-- 5. Tạo index để tối ưu hóa query
CREATE INDEX IX_bus_categories_owner_id ON bus_categories(owner_id);

-- 6. Kiểm tra kết quả
SELECT 
    bc.id,
    bc.name,
    bc.owner_id,
    u.email as owner_email
FROM bus_categories bc
LEFT JOIN users u ON bc.owner_id = u.id;

-- 7. Thống kê
SELECT 
    COUNT(*) as total_categories,
    COUNT(DISTINCT owner_id) as total_owners
FROM bus_categories;

-- =============================================
-- ROLLBACK SCRIPT (nếu cần hoàn tác):
-- =============================================
-- ALTER TABLE bus_categories DROP CONSTRAINT FK_bus_categories_owner_id;
-- DROP INDEX IX_bus_categories_owner_id ON bus_categories;
-- ALTER TABLE bus_categories DROP COLUMN owner_id;
