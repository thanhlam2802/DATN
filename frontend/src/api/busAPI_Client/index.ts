// Bus API Client - Main exports
export * from './locationApi'  
export * from './searchApi'
export * from './seatApi'

// Named exports to avoid conflicts
export { BookingAPI as LegacyBookingAPI } from './bookingApi'
export type { BookingResponse as LegacyBookingResponse } from './types'

// Bus Booking API exports (new system)
export * from './busbookingApi'