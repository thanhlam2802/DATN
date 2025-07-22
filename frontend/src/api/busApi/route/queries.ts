/**
 * GraphQL queries for Route operations
 */

import { gql } from '@apollo/client/core';
import { ROUTE_FRAGMENT } from '../fragments';

export const FIND_ALL_ROUTES = gql`
  query FindAllRoutes {
    findAllRoutes {
      ...RouteFragment
    }
  }
  ${ROUTE_FRAGMENT}
`;

export const FIND_ROUTE_BY_ID = gql`
  query FindRouteById($id: ID!) {
    findRouteById(id: $id) {
      ...RouteFragment
    }
  }
  ${ROUTE_FRAGMENT}
`;

export const FIND_BY_ORIGIN_AND_DESTINATION = `
  ${ROUTE_FRAGMENT}
  
  query FindByOriginAndDestination($origin: String!, $destination: String!) {
    findByOriginAndDestination(origin: $origin, destination: $destination) {
      ...RouteInfo
    }
  }
`; 