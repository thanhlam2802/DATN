import { gql } from '@/api/graphqlClient'

// Fragment cho RouteBusCategoryPrice - khớp với backend schema
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
      id
      origin
      destination
      distanceKm
      estimatedDurationMinutes
      createdAt
      updatedAt
    }
    busCategory {
      id
      name
    }
  }
`;

// Query: Lấy tất cả quy tắc giá
export const FIND_ALL_ROUTE_BUS_CATEGORY_PRICES = gql`
  query FindAllRouteBusCategoryPrices {
    findAllRouteBusCategoryPrices {
      ...RouteBusCategoryPriceFragment
    }
  }
  ${ROUTE_BUS_CATEGORY_PRICE_FRAGMENT}
`

// Query: Lấy quy tắc giá theo ID
export const FIND_ROUTE_BUS_CATEGORY_PRICE_BY_ID = gql`
  query FindRouteBusCategoryPriceById($id: ID!) {
    findRouteBusCategoryPriceById(id: $id) {
      ...RouteBusCategoryPriceFragment
    }
  }
  ${ROUTE_BUS_CATEGORY_PRICE_FRAGMENT}
`

// Query: Tìm quy tắc giá hiệu lực cho tuyến đường và loại xe tại một ngày cụ thể
export const FIND_ACTIVE_ROUTE_BUS_CATEGORY_PRICE = gql`
  query FindActiveRouteBusCategoryPrice($routeId: ID!, $busCategoryId: ID!, $date: String!) {
    findActiveRouteBusCategoryPrice(routeId: $routeId, busCategoryId: $busCategoryId, date: $date) {
      ...RouteBusCategoryPriceFragment
    }
  }
  ${ROUTE_BUS_CATEGORY_PRICE_FRAGMENT}
` 