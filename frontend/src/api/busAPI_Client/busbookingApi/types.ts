// Bus Booking API Types

export interface BusBookingRequest {
  busSlotId: number
  selectedSeatIds: number[]
  customerId?: number
  notes?: string
  passengerName: string
  passengerPhone: string
  passengerEmail?: string
}

export interface CustomerInfo {
  fullName: string
  phoneNumber: string
  email?: string
  notes?: string
}

export interface SelectedSeat {
  id: number
  seatNumber: string
  price: number
  seatType: string
}

export interface BookingResponse {
  id: number
  bookingReference: string
  status: string
  numPassengers: number
  totalPrice: number
  bookingDate: string
  expiresAt: string
  seatNumbers: string[]
  customer: {
    id: number
    fullName: string
    phone: string
    email?: string
  }
  busSlot: {
    id: number
    departureDate: string
    departureTime: string
    arrivalTime: string
    busName: string
    departureLocation: string
    arrivalLocation: string
  }
}

export interface CartResponse {
  id: number
  userId: number
  amount: number
  status: string
  busBookings: BookingResponse[]
  tourBookings: any[]
  flightBookings: any[]
  hotelBookings: any[]
}

export interface AddToCartRequest {
  itemId: number
  itemType: 'BUS'
  customerId?: number
  selectedSeatIds: number[]
  totalPrice: number
  notes?: string
  passengerName: string
  passengerPhone: string
  passengerEmail?: string
}

export interface ApiResponse<T = any> {
  status: string
  message: string
  data: T
}

export interface ValidationResult {
  valid: boolean
  errors: string[]
}

export interface BookingStep {
  step: number
  title: string
  isComplete: boolean
  isActive: boolean
  canAccess: boolean
} 