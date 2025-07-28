/**
 * Common shared types
 */

export interface User {
  id: string;
  name: string;
  email: string;
}

export interface Image {
  id: string;
  url: string;
  altText?: string;
  publicId: string;
}

export interface BusCategory {
  id: string;
  name: string;
}

export interface BusAmenity {
  id: string;
  name: string;
  description?: string;
  createdAt?: string;
  updatedAt?: string;
}

export interface Location {
  id: string;
  name: string;
  provinceCity?: string;
  district?: string;
  addressDetails?: string;
  createdAt?: string;
  updatedAt?: string;
}

export interface Bus {
  id: string;
  name: string;
  licensePlate: string;
  totalSeats: number;
  category: BusCategory;
  categoryName?: string; // Thêm lại categoryName
  owner: User;
  busImages: Image[];
  amenities?: BusAmenity[]; // Thêm amenities field
  createdAt?: string | null;
  updatedAt?: string | null;
}

export interface Route {
  id: string;
  originLocation: Location;
  destinationLocation: Location;
  distanceKm: number;
  estimatedDurationMinutes: number;
  createdAt?: string;
  updatedAt?: string;
}

// GraphQL Response wrapper
export interface GraphQLResponse<T> {
  data?: T;
  errors?: Array<{
    message: string;
    path?: string[];
  }>;
}

// === BusSlot Types ===
export enum BusSlotStatus {
  SCHEDULED = 'SCHEDULED',      // Đã được lên lịch, chưa khởi hành
  IN_PROGRESS = 'IN_PROGRESS',  // Đang hoạt động/đang chạy
  COMPLETED = 'COMPLETED',      // Đã hoàn thành chuyến đi
  CANCELLED = 'CANCELLED',      // Đã bị hủy
  DELAYED = 'DELAYED',          // Bị trì hoãn
  ARCHIVED = 'ARCHIVED'         // Đã lưu trữ (cho old trips)
}

export enum DelayReason {
  TRAFFIC_JAM = 'TRAFFIC_JAM',        // Kẹt xe
  WEATHER = 'WEATHER',                // Thời tiết xấu
  VEHICLE_ISSUE = 'VEHICLE_ISSUE',    // Sự cố xe
  PASSENGER_DELAY = 'PASSENGER_DELAY', // Khách trễ
  ROAD_ACCIDENT = 'ROAD_ACCIDENT',    // Tai nạn giao thông
  FUEL_STOP = 'FUEL_STOP',            // Dừng đổ xăng
  DRIVER_BREAK = 'DRIVER_BREAK',      // Nghỉ giải lao
  EARLY_ARRIVAL = 'EARLY_ARRIVAL',    // Đến sớm
  OTHER = 'OTHER'                     // Lý do khác
}

export enum TripType {
  ONE_TIME = 'ONE_TIME',     // Chuyến một lần
  RECURRING = 'RECURRING'    // Chuyến định kỳ
}

export enum RecurringPattern {
  DAILY = 'DAILY',           // Hàng ngày
  WEEKLY = 'WEEKLY',         // Hàng tuần
  WEEKDAYS = 'WEEKDAYS',     // Thứ 2-6
  WEEKENDS = 'WEEKENDS'      // Thứ 7, CN
}

export enum RealTimeStatus {
  ON_SCHEDULE = 'ON_SCHEDULE', // Đúng giờ
  EARLY = 'EARLY',             // Sớm hơn dự kiến
  DELAYED = 'DELAYED',         // Trễ hơn dự kiến
  UNKNOWN = 'UNKNOWN'          // Chưa xác định
}

export interface BusSlot {
  id: string;
  bus: Bus;
  route: Route;
  
  // Scheduled times (dự kiến)
  departureTime: string;
  arrivalTime: string;
  
  // Actual times (thực tế) - for early/late scenarios
  actualDepartureTime?: string;
  actualArrivalTime?: string;
  
  basePrice: number;
  availableSeats: number;
  totalSeats: number;
  status: BusSlotStatus;
  
  // New fields for auto-management
  tripType: TripType;
  recurringPattern?: RecurringPattern;
  recurringEndDate?: string;
  autoStatusUpdate: boolean;    // Tự động update status
  autoResetSeats: boolean;     // Tự động reset ghế cho chuyến tiếp theo
  
  // Real-time management
  allowManualOverride: boolean; // Cho phép manual update status
  timeToleranceMinutes: number; // Grace period (±30 phút)
  driverContactInfo?: string;   // Thông tin liên hệ tài xế
  currentLocation?: string;     // Vị trí hiện tại (optional GPS)
  delayReason?: string;        // Lý do delay nếu có
  
  createdAt: string;
  updatedAt: string;
}

// === Common Error Types ===
export interface ApiError {
  message: string
  code?: string
  details?: any
} 

// Response types
export interface BusSlotResponse {
  id: string
  bus: BusResponse
  route: RouteResponse
  slotDate: string
  
  // Scheduled vs Actual Times
  departureTime: string
  arrivalTime: string
  actualDepartureTime?: string
  actualArrivalTime?: string
  
  // Basic Info
  price: number
  totalSeats: number
  availableSeats: number
  status: BusSlotStatus
  
  // Real-time Management Fields
  delayReason?: DelayReason
  currentLocation?: string
  driverContactInfo?: string
  allowManualOverride: boolean
  timeToleranceMinutes: number
  
  // Auto-management Fields
  tripType: TripType
  recurringPattern?: RecurringPattern
  recurringEndDate?: string
  autoStatusUpdate: boolean
  autoResetSeats: boolean
  
  // Timestamps
  createdAt?: string
  updatedAt?: string
}

export interface BusResponse {
  id: string
  name: string
  licensePlate: string
  totalSeats: number
  categoryId: string
  categoryName?: string
  ownerId: string
  ownerName?: string
  createdAt?: string
  updatedAt?: string
}

export interface RouteResponse {
  id: string
  origin: string
  destination: string
  distanceKm?: number
  estimatedDurationMinutes?: number
  createdAt?: string
  updatedAt?: string
}

// Request types
export interface UpdateActualTimesRequest {
  actualDepartureTime?: string
  actualArrivalTime?: string
  delayReason?: DelayReason
  currentLocation?: string
  driverNotes?: string
}

export interface QuickStatusUpdateRequest {
  status: BusSlotStatus
  delayReason?: DelayReason
  currentLocation?: string
  driverNotes?: string
  autoSetActualTime?: boolean
}

export interface CreateBusSlotRequest {
  busId: string
  routeId: string
  slotDate: string
  departureTime: string
  arrivalTime: string
  price: number
  totalSeats: number
}

export interface UpdateBusSlotRequest {
  busId?: string
  routeId?: string
  slotDate?: string
  departureTime?: string
  arrivalTime?: string
  actualDepartureTime?: string
  actualArrivalTime?: string
  price?: number
  totalSeats?: number
  availableSeats?: number
  status?: BusSlotStatus
  
  // Real-time Management Fields
  delayReason?: DelayReason
  currentLocation?: string
  driverContactInfo?: string
  allowManualOverride?: boolean
  timeToleranceMinutes?: number
  
  // Auto-management Fields
  tripType?: TripType
  recurringPattern?: RecurringPattern
  recurringEndDate?: string
  autoStatusUpdate?: boolean
  autoResetSeats?: boolean
} 