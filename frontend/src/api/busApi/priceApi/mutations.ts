import { gql } from '@/api/graphqlClient'
import { ROUTE_BUS_CATEGORY_PRICE_FRAGMENT } from './queries'

// Mutation: Tạo quy tắc giá mới
export const CREATE_ROUTE_BUS_CATEGORY_PRICE = gql`
  mutation CreateRouteBusCategoryPrice($input: CreateRouteBusCategoryPriceInput!) {
    createRouteBusCategoryPrice(input: $input) {
      ...RouteBusCategoryPriceFragment
    }
  }
  ${ROUTE_BUS_CATEGORY_PRICE_FRAGMENT}
`

// Mutation: Cập nhật quy tắc giá
export const UPDATE_ROUTE_BUS_CATEGORY_PRICE = gql`
  mutation UpdateRouteBusCategoryPrice($id: ID!, $input: UpdateRouteBusCategoryPriceInput!) {
    updateRouteBusCategoryPrice(id: $id, input: $input) {
      ...RouteBusCategoryPriceFragment
    }
  }
  ${ROUTE_BUS_CATEGORY_PRICE_FRAGMENT}
`

// Mutation: Xóa quy tắc giá
export const DELETE_ROUTE_BUS_CATEGORY_PRICE = gql`
  mutation DeleteRouteBusCategoryPrice($id: ID!) {
    deleteRouteBusCategoryPrice(id: $id)
  }
` 