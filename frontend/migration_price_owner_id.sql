-- ======================================
-- MIGRATION: Thêm owner_id vào route_bus_category_prices
-- ======================================

-- 1. Thêm cột owner_id vào bảng route_bus_category_prices
ALTER TABLE route_bus_category_prices 
ADD COLUMN owner_id BIGINT;

-- 2. Tạo khóa ngoại liên kết với users
ALTER TABLE route_bus_category_prices 
ADD CONSTRAINT fk_price_owner_id 
FOREIGN KEY (owner_id) REFERENCES users(id);

-- 3. Cập nhật dữ liệu hiện có: lấy owner_id từ route
UPDATE route_bus_category_prices rp 
SET owner_id = (
    SELECT r.owner_id 
    FROM routes r 
    WHERE r.id = rp.route_id
)
WHERE rp.owner_id IS NULL;

-- 4. Thiết lập NOT NULL sau khi cập nhật dữ liệu
ALTER TABLE route_bus_category_prices 
ALTER COLUMN owner_id SET NOT NULL;

-- 5. Tạo index để tối ưu performance
CREATE INDEX idx_price_owner_id ON route_bus_category_prices(owner_id);
CREATE INDEX idx_price_owner_route ON route_bus_category_prices(owner_id, route_id);

-- ======================================
-- ROLLBACK SCRIPT (nếu cần)
-- ======================================
/*
-- Xóa constraints và indexes
DROP INDEX IF EXISTS idx_price_owner_route;
DROP INDEX IF EXISTS idx_price_owner_id;
ALTER TABLE route_bus_category_prices DROP CONSTRAINT IF EXISTS fk_price_owner_id;

-- Xóa cột
ALTER TABLE route_bus_category_prices DROP COLUMN IF EXISTS owner_id;
*/
