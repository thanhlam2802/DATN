import { BUS_CATEGORY_FRAGMENT } from '../fragments';

export const CREATE_BUS_CATEGORY = `
  mutation CreateBusCategory($input: CreateBusCategoryInput!) {
    createBusCategory(input: $input) {
      ...BusCategoryFragment
    }
  }
  ${BUS_CATEGORY_FRAGMENT}
`;

export const UPDATE_BUS_CATEGORY = `
  mutation UpdateBusCategory($id: ID!, $input: UpdateBusCategoryInput!) {
    updateBusCategory(id: $id, input: $input) {
      ...BusCategoryFragment
    }
  }
  ${BUS_CATEGORY_FRAGMENT}
`;

export const DELETE_BUS_CATEGORY = `
  mutation DeleteBusCategory($id: ID!) {
    deleteBusCategory(id: $id)
  }
`; 