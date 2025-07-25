import { gql } from '../../graphqlClient';
import { BUS_FRAGMENT, BUS_FRAGMENT_SIMPLE, BUS_FRAGMENT_WITH_IMAGES } from '../fragments';

export const FIND_ALL_BUSES = gql`
  query FindAllBuses {
    findAllBuses {
      ...BusFragment
    }
  }
  ${BUS_FRAGMENT}
`;

// Debug version với simple fragment
export const FIND_ALL_BUSES_SIMPLE = gql`
  query FindAllBusesSimple {
    findAllBuses {
      ...BusFragmentSimple
    }
  }
  ${BUS_FRAGMENT_SIMPLE}
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

// Debug version với simple fragment
export const FIND_BUSES_BY_OWNER_ID_WITH_SIMPLE_FRAGMENT = gql`
  query FindBusesByOwnerIdSimple($ownerId: ID!) {
    findBusesByOwnerId(ownerId: $ownerId) {
      ...BusFragmentSimple
    }
  }
  ${BUS_FRAGMENT_SIMPLE}
`;

// Backup query để test - không dùng fragment
export const FIND_BUSES_BY_OWNER_ID_SIMPLE = gql`
  query FindBusesByOwnerIdSimple($ownerId: ID!) {
    findBusesByOwnerId(ownerId: $ownerId) {
      id
      name
      licensePlate
      totalSeats
      categoryId
      categoryName
      ownerId
      ownerName
      createdAt
      updatedAt
    }
  }
`; 