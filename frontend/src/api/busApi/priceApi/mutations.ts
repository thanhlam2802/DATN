import { gql } from '@/api/graphqlClient'

// Mutation: Tạo quy tắc giá mới (FIXED with complete response data)
export const CREATE_ROUTE_BUS_CATEGORY_PRICE = gql`
  mutation CreateRouteBusCategoryPrice($input: CreateRouteBusCategoryPriceInput!) {
    createRouteBusCategoryPrice(input: $input) {
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
        originLocation {
          id
          name
        }
        destinationLocation {
          id
          name
        }
      }
      busCategory {
        id
        name
      }
    }
  }
`

// Mutation: Cập nhật quy tắc giá
export const UPDATE_ROUTE_BUS_CATEGORY_PRICE = gql`
  mutation UpdateRouteBusCategoryPrice($id: ID!, $input: UpdateRouteBusCategoryPriceInput!) {
    updateRouteBusCategoryPrice(id: $id, input: $input) {
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
        originLocation {
          id
          name
        }
        destinationLocation {
          id
          name
        }
      }
      busCategory {
        id
        name
      }
    }
  }
`

// Mutation: Xóa quy tắc giá
export const DELETE_ROUTE_BUS_CATEGORY_PRICE = gql`
  mutation DeleteRouteBusCategoryPrice($id: ID!) {
    deleteRouteBusCategoryPrice(id: $id)
  }
` 