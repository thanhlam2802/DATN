// Price Management Types based on GraphQL schema price.graphqls

import { Route, BusCategory } from '../types/common.types'

export interface RouteBusCategoryPrice {
  id: string
  route: Route
  busCategory: BusCategory
  basePrice: number
  promotionPrice?: number | null
  validFrom: string        // YYYY-MM-DD format
  validTo: string          // YYYY-MM-DD format
  notes?: string | null
  createdAt?: string
  updatedAt?: string
}

export interface CreateRouteBusCategoryPriceInput {
  routeId: string
  busCategoryId: string
  basePrice: number
  promotionPrice?: number | null
  validFrom: string        // YYYY-MM-DD format
  validTo: string          // YYYY-MM-DD format
  notes?: string | null
}

export interface UpdateRouteBusCategoryPriceInput {
  routeId?: string
  busCategoryId?: string
  basePrice?: number
  promotionPrice?: number | null
  validFrom?: string       // YYYY-MM-DD format
  validTo?: string         // YYYY-MM-DD format
  notes?: string | null
}

// Frontend UI specific types
export interface PriceFilters {
  route: string
  busCategory: string
  status: string
  dateRange?: {
    from: string
    to: string
  }
}

export interface PriceBulkUpdate {
  type: 'percentage' | 'fixed'
  value: number
  applyTo: 'all' | 'route' | 'busCategory'
  routeId?: string
  busCategoryId?: string
}

export enum PriceStatus {
  ACTIVE = 'active',          // Đang hiệu lực
  SCHEDULED = 'scheduled',    // Sắp có hiệu lực
  EXPIRED = 'expired'         // Đã hết hiệu lực
}

// Helper interface for price calculation
export interface PriceCalculationRequest {
  routeId: string
  busCategoryId: string
  date: string             // YYYY-MM-DD format
}

export interface PriceCalculationResult {
  found: boolean
  price?: RouteBusCategoryPrice
  effectivePrice: number   // Giá cuối cùng (promotion price nếu có, ngược lại base price)
  isPromotionPrice: boolean
  message?: string
} 