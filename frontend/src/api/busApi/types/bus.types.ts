/**
 * Bus-related TypeScript types
 */

import type { BusCategory, User, GraphQLResponse } from './common.types'

// === Bus Entity ===
export interface Bus {
  id: string
  name: string
  origin: string
  destination: string
  departureTime: string
  arrivalTime: string
  category: BusCategory
  categoryName: string
  owner: User
  createdAt?: string
  updatedAt?: string
}

// === Bus Input Types ===
export interface CreateBusInput {
  name: string
  origin: string
  destination: string
  departureTime: string
  arrivalTime: string
  categoryId: string
}

export interface UpdateBusInput {
  name?: string
  origin?: string
  destination?: string
  departureTime?: string
  arrivalTime?: string
  categoryId?: string
  ownerId?: string
  licensePlate?: string
  totalSeats?: number
  imageIds?: string[]
  imagesToDelete?: string[] // IDs of images to delete
}

// === Bus Response Types ===
export interface BusResponse {
  createBus?: Bus
  updateBus?: Bus
  deleteBus?: boolean
  findAllBuses?: Bus[]
  findBusById?: Bus
  findBusByOwner_Id?: Bus[]
}

// === Bus GraphQL Responses ===
export type BusGraphQLResponse = GraphQLResponse<BusResponse> 