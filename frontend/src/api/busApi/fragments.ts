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
    ownerId
  }
`;

export const BUS_AMENITY_FRAGMENT = gql`
  fragment BusAmenityFragment on BusAmenity {
    id
    name
    description
    createdAt
    updatedAt
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
    busImages {
      busId
      imageId
      image {
        ...ImageFragment
      }
    }
    amenities {
      ...BusAmenityFragment
    }
    createdAt
    updatedAt
  }
  ${IMAGE_FRAGMENT}
  ${BUS_AMENITY_FRAGMENT}
`;

// Simple fragment without complex nested fields for debugging
export const BUS_FRAGMENT_SIMPLE = gql`
  fragment BusFragmentSimple on Bus {
    id
    name
    licensePlate
    totalSeats
    categoryId
    categoryName
    ownerId
    createdAt
    updatedAt
  }
`;

// Fragment with images but no amenities
export const BUS_FRAGMENT_WITH_IMAGES = gql`
  fragment BusFragmentWithImages on Bus {
    id
    name
    licensePlate
    totalSeats
    categoryId
    categoryName
    ownerId
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
    ownerId
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