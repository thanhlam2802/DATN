import { gql } from '../../graphqlClient'
import { ROUTE_FRAGMENT } from '../route/fragments'

// Basic CRUD mutations
export const CREATE_BUS_SLOT = gql`
  mutation CreateBusSlot($input: CreateBusSlotInput!) {
    createBusSlot(input: $input) {
      id
      status
      bus {
        id
        name
        licensePlate
      }
      route {
        ...RouteInfo
      }
      slotDate
      departureTime
      arrivalTime
      price
      totalSeats
      availableSeats
      tripType
      allowManualOverride
      timeToleranceMinutes
      autoStatusUpdate
      autoResetSeats
      createdAt
      updatedAt
    }
  }
  ${ROUTE_FRAGMENT}
`

export const UPDATE_BUS_SLOT = gql`
  mutation UpdateBusSlot($id: ID!, $input: UpdateBusSlotInput!) {
    updateBusSlot(id: $id, input: $input) {
      id
      status
      bus {
        id
        name
        licensePlate
      }
      route {
        ...RouteInfo
      }
      slotDate
      departureTime
      arrivalTime
      actualDepartureTime
      actualArrivalTime
      price
      totalSeats
      availableSeats
      delayReason
      currentLocation
      driverContactInfo
      allowManualOverride
      timeToleranceMinutes
      tripType
      recurringPattern
      recurringEndDate
      autoStatusUpdate
      autoResetSeats
      createdAt
      updatedAt
    }
  }
  ${ROUTE_FRAGMENT}
`

export const DELETE_BUS_SLOT = gql`
  mutation DeleteBusSlot($id: ID!) {
    deleteBusSlot(id: $id)
  }
`

// Legacy status update mutations
export const ACTIVATE_BUS_SLOT = gql`
  mutation ActivateBusSlot($id: ID!) {
    activateBusSlot(id: $id) {
      id
      status
      actualDepartureTime
      updatedAt
    }
  }
`

export const COMPLETE_BUS_SLOT = gql`
  mutation CompleteBusSlot($id: ID!) {
    completeBusSlot(id: $id) {
      id
      status
      actualArrivalTime
      updatedAt
    }
  }
`

export const CANCEL_BUS_SLOT = gql`
  mutation CancelBusSlot($id: ID!) {
    cancelBusSlot(id: $id) {
      id
      status
      updatedAt
    }
  }
`

// Real-time Management mutations
export const UPDATE_ACTUAL_TIMES = gql`
  mutation UpdateActualTimes($id: ID!, $input: UpdateActualTimesInput!) {
    updateActualTimes(id: $id, input: $input) {
      id
      actualDepartureTime
      actualArrivalTime
      delayReason
      currentLocation
      status
      updatedAt
    }
  }
`

export const QUICK_STATUS_UPDATE = gql`
  mutation QuickStatusUpdate($id: ID!, $input: QuickStatusUpdateInput!) {
    quickStatusUpdate(id: $id, input: $input) {
      id
      status
      actualDepartureTime
      actualArrivalTime
      delayReason
      currentLocation
      updatedAt
    }
  }
`

export const MARK_BUS_SLOT_IN_PROGRESS = gql`
  mutation MarkBusSlotInProgress($id: ID!) {
    markBusSlotInProgress(id: $id) {
      id
      status
      actualDepartureTime
      updatedAt
    }
  }
`

export const MARK_BUS_SLOT_COMPLETED = gql`
  mutation MarkBusSlotCompleted($id: ID!) {
    markBusSlotCompleted(id: $id) {
      id
      status
      actualArrivalTime
      updatedAt
    }
  }
`

export const ARCHIVE_BUS_SLOT = gql`
  mutation ArchiveBusSlot($id: ID!) {
    archiveBusSlot(id: $id) {
      id
      status
      updatedAt
    }
  }
` 