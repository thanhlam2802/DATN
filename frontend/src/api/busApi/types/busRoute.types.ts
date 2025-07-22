/**
 * BusRoute-related TypeScript types
 */

import type { Bus } from './bus.types'
import type { Route } from './route.types'
import type { GraphQLResponse } from './common.types'

// === BusRoute Entity ===
export interface BusRoute {
  id: string
  bus: Bus
  route: Route
  createdAt?: string
  updatedAt?: string
}

// === BusRoute Input Types ===
export interface CreateBusRouteInput {
  busId: string
  routeId: string
}

export interface UpdateBusRouteInput {
  id: string
  busId?: string
  routeId?: string
}

// === BusRoute Response Types ===
export interface BusRouteResponse {
  createBusRoute?: BusRoute
  updateBusRoute?: BusRoute
  deleteBusRoute?: boolean
  findAllBusRoutes?: BusRoute[]
  findBusRouteById?: BusRoute
  findBusRoutesByBusId?: BusRoute[]
  findBusRoutesByRouteId?: BusRoute[]
}

// === BusRoute GraphQL Responses ===
export type BusRouteGraphQLResponse = GraphQLResponse<BusRouteResponse> 