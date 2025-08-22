-- ======================================
-- MIGRATION: Thêm owner_id vào bus_slots
-- ======================================

-- 1. Thêm cột owner_id vào bảng bus_slots
ALTER TABLE bus_slots 
ADD COLUMN owner_id BIGINT;

-- 2. Tạo khóa ngoại liên kết với users
ALTER TABLE bus_slots 
ADD CONSTRAINT fk_bus_slots_owner_id 
FOREIGN KEY (owner_id) REFERENCES users(id);

-- 3. Cập nhật dữ liệu hiện có: lấy owner_id từ bus
UPDATE bus_slots bs 
SET owner_id = (
    SELECT b.owner_id 
    FROM buses b 
    WHERE b.id = bs.bus_id
)
WHERE bs.owner_id IS NULL;

-- 4. Thiết lập NOT NULL sau khi cập nhật dữ liệu
ALTER TABLE bus_slots 
ALTER COLUMN owner_id SET NOT NULL;

-- 5. Tạo index để tối ưu performance
CREATE INDEX idx_bus_slots_owner_id ON bus_slots(owner_id);
CREATE INDEX idx_bus_slots_owner_slot_date ON bus_slots(owner_id, slot_date);

-- ======================================
-- ROLLBACK SCRIPT (nếu cần)
-- ======================================
/*
-- Xóa constraints và indexes
DROP INDEX IF EXISTS idx_bus_slots_owner_slot_date;
DROP INDEX IF EXISTS idx_bus_slots_owner_id;
ALTER TABLE bus_slots DROP CONSTRAINT IF EXISTS fk_bus_slots_owner_id;

-- Xóa cột
ALTER TABLE bus_slots DROP COLUMN IF EXISTS owner_id;
*/
