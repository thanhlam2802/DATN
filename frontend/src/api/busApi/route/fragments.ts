/**
 * GraphQL fragments for Route operations
 */

export const ROUTE_FRAGMENT = `
  fragment RouteInfo on Route {
    id
    origin
    destination
    distanceKm
    estimatedDurationMinutes
    createdAt
    updatedAt
  }
` 