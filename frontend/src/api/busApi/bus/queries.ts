import { gql } from '../../graphqlClient';
import { BUS_FRAGMENT } from '../fragments';

export const FIND_ALL_BUSES = gql`
  query FindAllBuses {
    findAllBuses {
      ...BusFragment
    }
  }
  ${BUS_FRAGMENT}
`;

export const FIND_BUS_BY_ID = gql`
  query FindBusById($id: ID!) {
    findBusById(id: $id) {
      ...BusFragment
    }
  }
  ${BUS_FRAGMENT}
`;

export const FIND_BUSES_BY_OWNER_ID = gql`
  query FindBusesByOwnerId($ownerId: ID!) {
    findBusesByOwnerId(ownerId: $ownerId) {
      ...BusFragment
    }
  }
  ${BUS_FRAGMENT}
`; 