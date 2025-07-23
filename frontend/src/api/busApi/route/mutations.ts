/**
 * GraphQL mutations for Route operations
 */

import { gql } from '../../graphqlClient';
import { ROUTE_FRAGMENT } from '../fragments';

export const CREATE_ROUTE = gql`
  mutation CreateRoute($input: CreateRouteInput!) {
    createRoute(input: $input) {
      ...RouteFragment
    }
  }
  ${ROUTE_FRAGMENT}
`;

export const UPDATE_ROUTE = gql`
  mutation UpdateRoute($id: ID!, $input: UpdateRouteInput!) {
    updateRoute(id: $id, input: $input) {
      ...RouteFragment
    }
  }
  ${ROUTE_FRAGMENT}
`;

export const DELETE_ROUTE = gql`
  mutation DeleteRoute($id: ID!) {
    deleteRoute(id: $id)
  }
`; 