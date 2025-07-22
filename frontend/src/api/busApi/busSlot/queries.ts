import { gql } from '@apollo/client/core'

// Fragment for complete BusSlot data with real-time fields
export const BUS_SLOT_DETAIL_FRAGMENT = gql`
  fragment BusSlotDetailFragment on BusSlot {
    id
    bus {
      id
      name
      licensePlate
      totalSeats
      categoryId
      categoryName
      ownerId
      ownerName
    }
    route {
      id
      origin
      destination
      distanceKm
      estimatedDurationMinutes
    }
    slotDate
    
    # Scheduled vs Actual Times
    departureTime
    arrivalTime
    actualDepartureTime
    actualArrivalTime
    
    # Basic Info
    price
    totalSeats
    availableSeats
    status
    
    # Real-time Management Fields
    delayReason
    currentLocation
    driverContactInfo
    allowManualOverride
    timeToleranceMinutes
    
    # Auto-management Fields
    tripType
    recurringPattern
    recurringEndDate
    autoStatusUpdate
    autoResetSeats
    
    # Timestamps
    createdAt
    updatedAt
  }
`

// Basic queries
export const FIND_ALL_BUS_SLOTS = gql`
  query FindAllBusSlots {
    findAllBusSlots {
      ...BusSlotDetailFragment
    }
  }
  ${BUS_SLOT_DETAIL_FRAGMENT}
`

export const FIND_BUS_SLOT_BY_ID = gql`
  query FindBusSlotById($id: ID!) {
    findBusSlotById(id: $id) {
      ...BusSlotDetailFragment
    }
  }
  ${BUS_SLOT_DETAIL_FRAGMENT}
`

export const FIND_BUS_SLOTS_BY_BUS_ID = gql`
  query FindBusSlotsByBusId($busId: ID!) {
    findBusSlotsByBusId(busId: $busId) {
      ...BusSlotDetailFragment
    }
  }
  ${BUS_SLOT_DETAIL_FRAGMENT}
`

export const FIND_BUS_SLOTS_BY_ROUTE_ID = gql`
  query FindBusSlotsByRouteId($routeId: ID!) {
    findBusSlotsByRouteId(routeId: $routeId) {
      ...BusSlotDetailFragment
    }
  }
  ${BUS_SLOT_DETAIL_FRAGMENT}
`

export const FIND_BUS_SLOTS_BY_STATUS = gql`
  query FindBusSlotsByStatus($status: BusSlotStatus!) {
    findBusSlotsByStatus(status: $status) {
      ...BusSlotDetailFragment
    }
  }
  ${BUS_SLOT_DETAIL_FRAGMENT}
`

// Advanced search
export const SEARCH_BUS_SLOTS = gql`
  query SearchBusSlots(
    $departureLocationId: String!
    $arrivalLocationId: String!
    $slotDate: String!
    $busCategoryId: ID
    $minPrice: BigDecimal
    $maxPrice: BigDecimal
    $minAvailableSeats: Int
    $status: BusSlotStatus
  ) {
    searchBusSlots(
      departureLocationId: $departureLocationId
      arrivalLocationId: $arrivalLocationId
      slotDate: $slotDate
      busCategoryId: $busCategoryId
      minPrice: $minPrice
      maxPrice: $maxPrice
      minAvailableSeats: $minAvailableSeats
      status: $status
    ) {
      ...BusSlotDetailFragment
    }
  }
  ${BUS_SLOT_DETAIL_FRAGMENT}
`

// Real-time Management queries
export const FIND_TRIPS_NEEDING_STATUS_UPDATE = gql`
  query FindTripsNeedingStatusUpdate {
    findTripsNeedingStatusUpdate {
      ...BusSlotDetailFragment
    }
  }
  ${BUS_SLOT_DETAIL_FRAGMENT}
`

export const FIND_COMPLETED_TRIPS_FOR_ARCHIVE = gql`
  query FindCompletedTripsForArchive($daysOld: Int) {
    findCompletedTripsForArchive(daysOld: $daysOld) {
      ...BusSlotDetailFragment
    }
  }
  ${BUS_SLOT_DETAIL_FRAGMENT}
` 