import { gql } from '../graphqlClient';

export const IMAGE_FRAGMENT = gql`
  fragment ImageFragment on ImageResponse {
    id
    url
    altText
    uploadedAt
    publicId
  }
`;

export const BUS_CATEGORY_FRAGMENT = gql`
  fragment BusCategoryFragment on BusCategory {
    id
    name
  }
`;

export const ROUTE_FRAGMENT = gql`
  fragment RouteFragment on Route {
    id
    origin
    destination
    distanceKm
    estimatedDurationMinutes
    createdAt
    updatedAt
  }
`;

export const BUS_FRAGMENT = gql`
  fragment BusFragment on Bus {
    id
    name
    licensePlate
    totalSeats
    categoryId
    categoryName
    ownerId
    ownerName
    busImages {
      busId
      imageId
      image {
        ...ImageFragment
      }
    }
    createdAt
    updatedAt
  }
  ${IMAGE_FRAGMENT}
`;

export const BUS_SLOT_FRAGMENT = gql`
  fragment BusSlotFragment on BusSlot {
    id
    bus {
      ...BusFragment
    }
    route {
      ...RouteFragment
    }
    slotDate
    departureTime
    arrivalTime
    actualDepartureTime
    actualArrivalTime
    price
    totalSeats
    availableSeats
    status
    createdAt
    updatedAt
  }
  ${BUS_FRAGMENT}
  ${ROUTE_FRAGMENT}
`; 