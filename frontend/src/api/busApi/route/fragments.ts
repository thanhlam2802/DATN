/**
 * GraphQL fragments for Route operations
 */

import { gql } from '@/api/graphqlClient'

// Fragment cho Location
export const LOCATION_FRAGMENT = gql`
  fragment LocationInfo on Location {
    id
    name
    provinceCity
    district
    addressDetails
    createdAt
    updatedAt
  }
`;

// Fragment cho Route (updated để sử dụng Location objects)
export const ROUTE_FRAGMENT = gql`
  fragment RouteInfo on Route {
    id
    originLocation {
      ...LocationInfo
    }
    destinationLocation {
      ...LocationInfo
    }
    ownerId
    distanceKm
    estimatedDurationMinutes
    createdAt
    updatedAt
  }
  ${LOCATION_FRAGMENT}
`; 