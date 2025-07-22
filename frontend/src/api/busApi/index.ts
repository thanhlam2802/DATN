/**
 * BusAPI - Unified Bus Management API
 * 
 * Provides complete TypeScript API for Bus, BusCategory, Route, and BusSlot operations
 * Based on backend DTOs and GraphQL schema
 * 
 * Usage:
 * ```typescript
 * import { BusAPI, BusSlotAPI, createBusCategory, getAllRoutes } from '@/api/busApi'
 * 
 * // Or import specific modules
 * import { BusAPI, BusCategoryAPI, RouteAPI, BusSlotAPI } from '@/api/busApi'
 * ```
 */

// === Main Bus API Module Exports ===

// Bus Management APIs
export { BusAPI } from './bus'
export { BusCategoryAPI } from './bus'

// BusSlot/Trip Management APIs  
export { BusSlotAPI } from './busSlot'

// Route Management APIs
export { RouteAPI } from './route'

// Types and Enums
export * from './types'

// Re-export common types for convenience
export type { 
  BusSlotResponse,
  CreateBusSlotRequest,
  UpdateBusSlotRequest,
  UpdateActualTimesRequest,
  QuickStatusUpdateRequest
} from './types/common.types'

export { 
  BusSlotStatus,
  DelayReason,
  TripType,
  RecurringPattern,
  RealTimeStatus
} from './types/common.types' 