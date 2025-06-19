-- Migration file created on 19/06/2025 at 14:32:43.06 

ALTER TABLE hotel_rooms
ADD room_quantity SMALLINT NOT NULL DEFAULT 1;
