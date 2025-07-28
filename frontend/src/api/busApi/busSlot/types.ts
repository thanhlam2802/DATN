import { BusSlot, BusSlotStatus } from '../types/common.types';

// === BusSlot Input Types ===

// Input cho Create BusSlot - Khớp với CreateBusSlotInput trong schema
export interface CreateBusSlotInput {
  busId: string;
  routeId: string;
  slotDate: string;        // Ngày chuyến đi (YYYY-MM-DD)
  departureTime: string;   // Thời gian khởi hành (HH:MM:SS)
  arrivalTime: string;     // Thời gian đến (HH:MM:SS)
  price: number;           // Giá vé
  totalSeats: number;      // Tổng số ghế
}

// Input cho Update BusSlot - Khớp với UpdateBusSlotInput trong schema
export interface UpdateBusSlotInput {
  busId?: string;
  routeId?: string;
  slotDate?: string;
  departureTime?: string;
  arrivalTime?: string;
  actualDepartureTime?: string;  // DateTime từ backend
  actualArrivalTime?: string;    // DateTime từ backend
  price?: number;
  totalSeats?: number;
  availableSeats?: number;
  status?: BusSlotStatus;
}

// === Search Input Types ===

// Input cho searchBusSlots - Khớp với schema
export interface SearchBusSlotsInput {
  departureLocationId: string;    // ID của điểm đi (từ Route)
  arrivalLocationId: string;      // ID của điểm đến (từ Route)
  slotDate: string;               // Ngày khởi hành (YYYY-MM-DD)
  busCategoryId?: string;         // Tùy chọn: ID loại xe
  minPrice?: number;              // Tùy chọn: Giá tối thiểu
  maxPrice?: number;              // Tùy chọn: Giá tối đa
  minAvailableSeats?: number;     // Tùy chọn: Số ghế trống tối thiểu
  status?: BusSlotStatus;         // Tùy chọn: Trạng thái
}

// === Re-export Common Types ===
export type { BusSlot };
export { BusSlotStatus }; 