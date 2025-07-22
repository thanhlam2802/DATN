import { gql } from '@apollo/client/core';
import { BUS_FRAGMENT } from '../fragments';

export const CREATE_BUS = gql`
  mutation CreateBus($input: CreateBusInput!) {
    createBus(input: $input) {
      ...BusFragment
    }
  }
  ${BUS_FRAGMENT}
`;

export const UPDATE_BUS = gql`
  mutation UpdateBus($id: ID!, $input: UpdateBusInput!) {
    updateBus(id: $id, input: $input) {
      ...BusFragment
    }
  }
  ${BUS_FRAGMENT}
`;

export const DELETE_BUS = gql`
  mutation DeleteBus($id: ID!) {
    deleteBus(id: $id)
  }
`; 