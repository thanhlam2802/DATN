import { gql } from '@/api/graphqlClient'

// Simplified Route fragment for Price operations (avoid nested complexity)
export const ROUTE_BASIC_FRAGMENT = gql`
  fragment RouteBasicInfo on Route {
    id
    originLocation {
      id
      name
    }
    destinationLocation {
      id
      name
    }
    distanceKm
    estimatedDurationMinutes
  }
`;

// Fragment cho RouteBusCategoryPrice - FIXED to match backend schema
export const ROUTE_BUS_CATEGORY_PRICE_FRAGMENT = gql`
  fragment RouteBusCategoryPriceFragment on RouteBusCategoryPrice {
    id
    basePrice
    promotionPrice
    validFrom
    validTo
    notes
    createdAt
    updatedAt
    route {
      ...RouteBasicInfo
    }
    busCategory {
      id
      name
    }
  }
  ${ROUTE_BASIC_FRAGMENT}
`;

// Query: Lấy tất cả quy tắc giá
export const FIND_ALL_ROUTE_BUS_CATEGORY_PRICES = gql`
  query FindAllRouteBusCategoryPrices {
    findAllRouteBusCategoryPrices {
      ...RouteBusCategoryPriceFragment
    }
  }
  ${ROUTE_BUS_CATEGORY_PRICE_FRAGMENT}
`;

// Query: Lấy quy tắc giá theo ID
export const FIND_ROUTE_BUS_CATEGORY_PRICE_BY_ID = gql`
  query FindRouteBusCategoryPriceById($id: ID!) {
    findRouteBusCategoryPriceById(id: $id) {
      ...RouteBusCategoryPriceFragment
    }
  }
  ${ROUTE_BUS_CATEGORY_PRICE_FRAGMENT}
`;

// Query: Tìm quy tắc giá hiệu lực cho tuyến đường và loại xe tại một ngày cụ thể
export const FIND_ACTIVE_ROUTE_BUS_CATEGORY_PRICE = gql`
  query FindActiveRouteBusCategoryPrice($routeId: ID!, $busCategoryId: ID!, $date: String!) {
    findActiveRouteBusCategoryPrice(routeId: $routeId, busCategoryId: $busCategoryId, date: $date) {
      ...RouteBusCategoryPriceFragment
    }
  }
  ${ROUTE_BUS_CATEGORY_PRICE_FRAGMENT}
`;

// ✅ THÊM MỚI: Query cho findPricesByOwnerIdAndRoute
export const FIND_PRICES_BY_OWNER_ID_AND_ROUTE = gql`
  query FindPricesByOwnerIdAndRoute($ownerId: ID!, $routeId: ID!) {
    findPricesByOwnerIdAndRoute(ownerId: $ownerId, routeId: $routeId) {
      ...RouteBusCategoryPriceFragment
    }
  }
  ${ROUTE_BUS_CATEGORY_PRICE_FRAGMENT}
`; 