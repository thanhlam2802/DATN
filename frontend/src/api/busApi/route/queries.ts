/**
 * GraphQL queries for Route operations
 */

import { gql } from '../../graphqlClient';
import { ROUTE_FRAGMENT, LOCATION_FRAGMENT } from './fragments';

export const FIND_ALL_ROUTES = gql`
  query FindAllRoutes {
    findAllRoutes {
      ...RouteInfo
    }
  }
  ${ROUTE_FRAGMENT}
`;

export const FIND_ROUTE_BY_ID = gql`
  query FindRouteById($id: ID!) {
    findRouteById(id: $id) {
      ...RouteInfo
    }
  }
  ${ROUTE_FRAGMENT}
`;

// ✅ THÊM MỚI: Query cho getRoutesByOwnerId
export const GET_ROUTES_BY_OWNER_ID = gql`
  query GetRoutesByOwnerId($ownerId: ID!) {
    getRoutesByOwnerId(ownerId: $ownerId) {
      ...RouteInfo
    }
  }
  ${ROUTE_FRAGMENT}
`;

export const FIND_BY_ORIGIN_AND_DESTINATION = gql`
  query FindByOriginAndDestination($origin: String!, $destination: String!) {
    findByOriginAndDestination(origin: $origin, destination: $destination) {
      ...RouteInfo
    }
  }
  ${ROUTE_FRAGMENT}
`; 