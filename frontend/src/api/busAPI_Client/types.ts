// Client-side Bus API Types

// Enums
export enum SeatStatus {
  AVAILABLE = 'available',
  OCCUPIED = 'occupied', 
  PENDING = 'pending',
  SELECTED = 'selected'
}

export enum BusSlotStatus {
  SCHEDULED = 'SCHEDULED',
  IN_PROGRESS = 'IN_PROGRESS',
  COMPLETED = 'COMPLETED',
  CANCELLED = 'CANCELLED',
  DELAYED = 'DELAYED'
}

// Base types
export interface Location {
  id: string
  name: string
  count?: number // số chuyến có sẵn
}

export interface BusInfo {
  id: string
  name: string
  licensePlate: string
  categoryName: string
  totalSeats: number
  busImages?: BusImage[]
}

export interface BusImage {
  id: string
  image: {
    url: string
    altText?: string
  }
}

export interface RouteInfo {
  id: string
  origin: string
  destination: string
  distanceKm?: number
  estimatedDurationMinutes?: number
}

// Main BusSlot for search results
export interface BusSlot {
  id: string
  slotDate: string
  departureTime: string
  arrivalTime: string
  price: number
  totalSeats: number
  availableSeats: number
  status: BusSlotStatus
  bus: BusInfo
  route: RouteInfo
  actualDepartureTime?: string
  actualArrivalTime?: string
  createdAt?: string
  updatedAt?: string
}

// Search related
export interface SearchCriteria {
  from: string // origin location
  to: string // destination location  
  departureDate: string // YYYY-MM-DD
  returnDate?: string // for roundtrip
  seats: number
  roundtrip: boolean
}

export interface SearchBusSlotsInput {
  origin: string
  destination: string
  slotDate: string
  minAvailableSeats?: number
  busCategoryId?: string
  minPrice?: number
  maxPrice?: number
  status?: BusSlotStatus
}

// Seat related
export interface SeatInfo {
  seatNumber: number
  status: SeatStatus
  passengerName?: string
  bookingId?: string
}

export interface SeatOccupancy {
  busSlotId: string
  totalSeats: number
  availableSeats: number
  occupiedSeats: number
  pendingSeats: number
  seatMap: SeatInfo[]
}

// Booking related
export interface PassengerInfo {
  fullName: string
  phoneNumber: string
  notes?: string
}

export interface BookingData {
  selectedSeats: number[]
  passengerInfo: PassengerInfo
  voucherCode?: string
  paymentMethod: string
  totalAmount: number
  discount: number
}

export interface CreateBookingInput {
  busSlotId: string
  seatNumbers: number[]
  passengerInfo: PassengerInfo
  paymentMethod: string
  totalAmount: number
  voucherCode?: string
}

// API Response types
export interface SearchResponse {
  busSlots: BusSlot[]
  total: number
}

export interface LocationResponse {
  locations: Location[]
}

export interface BookingResponse {
  bookingId: string
  busSlot: BusSlot
  seats: number[]
  passenger: PassengerInfo
  totalAmount: number
  status: string
  qrCode?: string
}

// Component props
export interface BusCardProps {
  trip: BusSlot
}

export interface SeatLayoutProps {
  busSlot: BusSlot
  selectedSeats: number[]
  onSeatSelect: (seatNumber: number) => void
} 