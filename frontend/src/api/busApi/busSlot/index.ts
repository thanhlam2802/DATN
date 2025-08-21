/**
 * This module centralizes and exports all APIs and types
 * related to BusSlot (Chuyến đi).
 */


// Export BusSlot API and types
export { BusSlotAPI } from './api'
export * from './queries'
export * from './mutations'

// Re-export common types for convenience
export type { 
  BusSlotResponse,
  CreateBusSlotRequest,
  UpdateBusSlotRequest,
  UpdateActualTimesRequest,
  QuickStatusUpdateRequest
} from '../types/common.types'

export { 
  BusSlotStatus,
  DelayReason,
  TripType,
  RecurringPattern,
  RealTimeStatus
} from '../types/common.types' 