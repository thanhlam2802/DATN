/**
 * Route-related TypeScript types
 */

import type { GraphQLResponse } from './common.types'

// === Route Entity ===
export interface Route {
  id: string
  origin: string
  destination: string
  distanceKm?: number
  estimatedDurationMinutes?: number
  createdAt?: string
  updatedAt?: string
}

// === Route Input Types ===
export interface CreateRouteInput {
  origin: string
  destination: string
  distanceKm?: number
  estimatedDurationMinutes?: number
}

export interface UpdateRouteInput {
  id: string
  origin?: string
  destination?: string
  distanceKm?: number
  estimatedDurationMinutes?: number
}

// === Route Response Types ===
export interface RouteResponse {
  createRoute?: Route
  updateRoute?: Route
  deleteRoute?: boolean
  findAllRoutes?: Route[]
  findRouteById?: Route
  findByOriginAndDestination?: Route
}

// === Route GraphQL Responses ===
export type RouteGraphQLResponse = GraphQLResponse<RouteResponse> 